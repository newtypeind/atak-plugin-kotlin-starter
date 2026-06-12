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
        L.d("onReceive SHOW_PLUGIN")
        controller.refreshLocation()

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
        composeHost?.dispose()
        composeHost = null
    }

    companion object {
        const val SHOW_PLUGIN = "com.atakmap.android.kotlinstarter.SHOW_PLUGIN"
    }
}
