package com.atakmap.android.kotlinstarter

import android.content.Context
import android.content.Intent
import com.atakmap.android.dropdown.DropDownMapComponent
import com.atakmap.android.ipc.AtakBroadcast.DocumentedIntentFilter
import com.atakmap.android.maps.MapView
import com.atakmap.android.kotlinstarter.util.L

/** Registers the DropDown receiver that hosts the Compose UI. */
class PluginMapComponent : DropDownMapComponent() {

    override fun onCreate(context: Context, intent: Intent, view: MapView) {
        context.setTheme(R.style.ATAKPluginTheme)
        super.onCreate(context, intent, view)
        L.d("MapComponent onCreate")
        val receiver = PluginDropDownReceiver(view, context)
        val filter = DocumentedIntentFilter()
        filter.addAction(PluginDropDownReceiver.SHOW_PLUGIN)
        registerDropDownReceiver(receiver, filter)
    }

    override fun onDestroyImpl(context: Context, view: MapView) {
        super.onDestroyImpl(context, view)
    }
}
