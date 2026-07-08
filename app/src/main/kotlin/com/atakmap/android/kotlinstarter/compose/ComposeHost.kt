package com.atakmap.android.kotlinstarter.compose

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Recomposer
import androidx.compose.ui.platform.AndroidUiDispatcher
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.atakmap.android.kotlinstarter.util.L
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Hosts Jetpack Compose inside ATAK.
 *
 * Pieces, each working around one ATAK-specific obstacle:
 *  1. [SelectiveLifecycle] - the ComposeView's ViewTree lifecycle owner. It
 *     drives Compose's WrappedComposition (so content actually composes) while
 *     never delivering onCreate to AndroidComposeView (whose onCreate resolves to
 *     ATAK's abstract method and would crash with AbstractMethodError).
 *  2. A private [Recomposer] on the main-thread frame clock, fed via
 *     setParentCompositionContext, so recomposition does not depend on ATAK's
 *     window recomposer machinery.
 *  3. A FrameLayout wrapper so the lifecycle owners ATAK forces onto the
 *     DropDown/Pane's root view land on the wrapper, leaving the inner
 *     ComposeView's owner (ours) intact.
 *
 * The caller must invoke [dispose] when the DropDown closes.
 */
class ComposeHost(private val context: Context) {

    private val lifecycleOwner = object : LifecycleOwner {
        val selective = SelectiveLifecycle(this)
        override val lifecycle: Lifecycle get() = selective
    }

    private var scope: CoroutineScope? = null
    private var recomposer: Recomposer? = null

    fun createView(content: @Composable () -> Unit): View {
        val composeView = ComposeView(context)
        composeView.setViewTreeLifecycleOwner(lifecycleOwner)

        val frameClockContext = AndroidUiDispatcher.Main
        val recomposer = Recomposer(frameClockContext)
        val scope = CoroutineScope(frameClockContext)
        this.recomposer = recomposer
        this.scope = scope

        composeView.setParentCompositionContext(recomposer)
        // Tie composition lifetime to OUR SelectiveLifecycle (disposed only in
        // [dispose]), not to view attach state — so an ATAK detach/reattach
        // (navigating to the Tools menu and back) doesn't blank the composition.
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed,
        )
        composeView.setContent(content)
        scope.launch { recomposer.runRecomposeAndApplyChanges() }

        // Advance to RESUMED now. WrappedComposition registers on attach and is
        // caught up to RESUMED (firing ON_CREATE -> it composes); AndroidComposeView
        // is filtered out by SelectiveLifecycle.
        lifecycleOwner.selective.moveToResumed()

        L.d("ComposeHost: view created (SelectiveLifecycle=RESUMED, manual recomposer)")

        return FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH, MATCH)
            addView(composeView, FrameLayout.LayoutParams(MATCH, MATCH))
        }
    }

    fun dispose() {
        lifecycleOwner.selective.moveToDestroyed()
        recomposer?.cancel()
        scope?.cancel()
        recomposer = null
        scope = null
        L.d("ComposeHost: disposed")
    }

    private companion object {
        const val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
    }
}
