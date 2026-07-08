package com.atakmap.android.kotlinstarter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.atakmap.android.dropdown.DropDown.OnStateListener
import com.atakmap.android.dropdown.DropDownReceiver
import com.atakmap.android.kotlinstarter.compose.ComposeHost
import com.atakmap.android.kotlinstarter.ui.PluginApp
import com.atakmap.android.kotlinstarter.util.L
import com.atakmap.android.maps.MapView

/**
 * Shows the Compose Material 3 UI inside an ATAK DropDown.
 *
 * The DropDown hosts a [ComposeHost]-managed ComposeView. Two details make
 * Compose behave here:
 *  - [ComposeContext] wraps the plugin context (so the plugin's + Compose's merged
 *    resources resolve) but returns ATAK's real Application from
 *    getApplicationContext(); the plugin context's own is null and would NPE.
 *  - [ComposeHost] gives the ComposeView a [com.atakmap.android.kotlinstarter.compose.SelectiveLifecycle]
 *    and a private Recomposer so ATAK's lifecycle/recomposer machinery never
 *    touches it (see ComposeHost for why).
 */
class PluginDropDownReceiver(
    mapView: MapView,
    private val pluginContext: Context,
) : DropDownReceiver(mapView), OnStateListener {

    private val controller = PluginController(mapView.context)
    private var composeHost: ComposeHost? = null

    /** Plugin context for resources, ATAK's Application for component callbacks. */
    private class ComposeContext(host: Context, plugin: Context) : ContextWrapper(plugin) {
        private val app = host.applicationContext
        override fun getApplicationContext(): Context = app
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != SHOW_PLUGIN) return
        L.d("onReceive SHOW_PLUGIN (visible=$isVisible)")
        controller.refreshLocation()

        // Already open with a live Compose view (e.g. re-tapped from the Tools
        // menu while the pane is still up): re-showing would dispose the live
        // host and ATAK may not re-attach the replacement, leaving a blank pane.
        // Keep the live view instead.
        if (isVisible && composeHost != null) {
            L.d("plugin already visible — keeping the live view")
            return
        }

        composeHost?.dispose()
        val host = ComposeHost(ComposeContext(mapView.context, pluginContext))
        composeHost = host
        val view = host.createView { PluginApp(controller) }

        showDropDown(view, HALF_WIDTH, FULL_HEIGHT, FULL_WIDTH, HALF_HEIGHT, false, this)
    }

    override fun disposeImpl() {
        composeHost?.dispose()
        composeHost = null
        controller.release()
    }

    override fun onDropDownSelectionRemoved() {}
    override fun onDropDownVisible(v: Boolean) {}
    override fun onDropDownSizeChanged(width: Double, height: Double) {}
    override fun onDropDownClose() {
        // Intentionally do NOT dispose the ComposeHost here. ATAK closes and then
        // immediately re-shows this DropDown through its own backstack (e.g. when
        // the tool is re-selected from the Tools menu). Because the ComposeView
        // now disposes its composition on SelectiveLifecycle destroy (see
        // ComposeHost), destroying the lifecycle on a transient close would make
        // that reattach reinstall the dispose-strategy on an already-destroyed
        // lifecycle and crash ("SelectiveLifecycle ... is already destroyed").
        // The host is disposed lazily on the next open (onReceive) and on teardown
        // (disposeImpl).
        L.d("onDropDownClose (host kept alive for possible reattach)")
    }

    companion object {
        const val SHOW_PLUGIN = "com.atakmap.android.kotlinstarter.SHOW_PLUGIN"
    }
}
