package com.atakmap.android.kotlinstarter.compose

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner

/**
 * A [Lifecycle] that dispatches events ONLY to [LifecycleEventObserver]s and
 * silently ignores raw [androidx.lifecycle.DefaultLifecycleObserver]s.
 *
 * This is the crux of running Compose inside ATAK. Two observers attach to the
 * ComposeView's lifecycle:
 *  - Compose's `WrappedComposition` (a LifecycleEventObserver) - it only performs
 *    the real composition once it receives ON_CREATE, so it MUST get events.
 *  - Compose's `AndroidComposeView` (a DefaultLifecycleObserver) - on this ATAK
 *    build its inherited onCreate resolves to an *abstract* method and throws
 *    AbstractMethodError the instant ON_CREATE is delivered.
 *
 * A real LifecycleRegistry wraps every observer in a DefaultLifecycleObserverAdapter
 * and would dispatch to both. This implementation instead keeps only the
 * LifecycleEventObservers, so Compose composes and AndroidComposeView is never
 * told onCreate. AndroidComposeView only uses those callbacks for debug overlays,
 * so nothing visible is lost.
 */
class SelectiveLifecycle(private val owner: LifecycleOwner) : Lifecycle() {

    private var stateField: State = State.INITIALIZED
    private val observers = LinkedHashSet<LifecycleEventObserver>()

    override val currentState: State
        get() = stateField

    override fun addObserver(observer: LifecycleObserver) {
        if (observer !is LifecycleEventObserver) return
        observers.add(observer)
        // Catch a late observer up to the current state.
        if (stateField.isAtLeast(State.CREATED)) observer.onStateChanged(owner, Event.ON_CREATE)
        if (stateField.isAtLeast(State.STARTED)) observer.onStateChanged(owner, Event.ON_START)
        if (stateField.isAtLeast(State.RESUMED)) observer.onStateChanged(owner, Event.ON_RESUME)
    }

    override fun removeObserver(observer: LifecycleObserver) {
        if (observer is LifecycleEventObserver) observers.remove(observer)
    }

    fun moveToResumed() {
        step(Event.ON_CREATE, State.CREATED)
        step(Event.ON_START, State.STARTED)
        step(Event.ON_RESUME, State.RESUMED)
    }

    fun moveToDestroyed() {
        if (stateField.isAtLeast(State.RESUMED)) step(Event.ON_PAUSE, State.STARTED)
        if (stateField.isAtLeast(State.STARTED)) step(Event.ON_STOP, State.CREATED)
        if (stateField.isAtLeast(State.CREATED)) step(Event.ON_DESTROY, State.DESTROYED)
    }

    private fun step(event: Event, target: State) {
        if (stateField == target) return
        stateField = target
        observers.toList().forEach { it.onStateChanged(owner, event) }
    }
}
