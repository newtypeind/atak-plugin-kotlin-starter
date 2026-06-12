package com.atakmap.android.kotlinstarter

import android.content.Context
import com.atak.plugins.impl.AbstractPluginTool
import gov.tak.api.util.Disposable

/**
 * Toolbar entry. Appears in ATAK's Tools menu with the plugin name/icon and, on
 * tap, broadcasts [PluginDropDownReceiver.SHOW_PLUGIN] to open the DropDown.
 */
class PluginTool(context: Context) : AbstractPluginTool(
    context,
    context.getString(R.string.app_name),
    context.getString(R.string.app_name),
    context.getDrawable(R.drawable.ic_launcher),
    PluginDropDownReceiver.SHOW_PLUGIN,
),
    Disposable {
    override fun dispose() {}
}
