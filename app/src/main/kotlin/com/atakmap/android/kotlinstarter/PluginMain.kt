package com.atakmap.android.kotlinstarter

import com.atak.plugins.impl.AbstractPlugin
import com.atak.plugins.impl.PluginContextProvider
import gov.tak.api.plugin.IServiceController

/**
 * Plugin entry point (declared in plugin.xml).
 *
 * Uses ATAK's legacy hybrid lifecycle: an [AbstractPlugin] that wires a toolbar
 * tool ([PluginTool]) to a [com.atakmap.android.dropdown.DropDownMapComponent]
 * ([PluginMapComponent]). Tapping the tool broadcasts an intent; the map
 * component's [PluginDropDownReceiver] shows the Compose UI in a DropDown.
 *
 * We deliberately avoid the newer IPlugin Pane API: ATAK wraps a Pane in its own
 * Fragment, whose FragmentViewLifecycleOwner drives the ComposeView through
 * ATAK's runtime androidx.lifecycle, whose DefaultLifecycleObserver.onCreate is
 * abstract - which crashes Compose's AndroidComposeView with AbstractMethodError.
 * A DropDown imposes no such lifecycle owner, so Compose uses the plugin's own
 * (correct) lifecycle classes.
 */
class PluginMain(serviceController: IServiceController) : AbstractPlugin(
    serviceController,
    PluginTool(
        serviceController.getService(PluginContextProvider::class.java).pluginContext,
    ),
    PluginMapComponent(),
)
