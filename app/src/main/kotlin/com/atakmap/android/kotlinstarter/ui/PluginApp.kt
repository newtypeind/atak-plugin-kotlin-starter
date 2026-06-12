package com.atakmap.android.kotlinstarter.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.atakmap.android.kotlinstarter.PluginController
import com.atakmap.android.kotlinstarter.compose.AppTheme
import com.atakmap.android.kotlinstarter.feature.main.MainScreen

/**
 * Top-level plugin UI. Owns the [AppTheme] wrapper so the light/dark choice (held
 * in [PluginController.isDarkTheme]) applies to every screen, then renders the
 * main screen. Add screen routing here as the plugin grows.
 */
@Composable
fun PluginApp(controller: PluginController) {
    val darkTheme by controller.isDarkTheme.collectAsState()
    AppTheme(darkTheme = darkTheme) {
        MainScreen(
            controller = controller,
            darkTheme = darkTheme,
            onToggleTheme = controller::toggleTheme,
        )
    }
}
