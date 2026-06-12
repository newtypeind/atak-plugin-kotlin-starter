package com.atakmap.android.kotlinstarter.feature.main

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.atakmap.android.kotlinstarter.PluginController

/**
 * The plugin's main screen. Thin stateful wrapper: it collects the controller's
 * [PluginController.state] flow and the local callsign text, then delegates to the
 * stateless [MainScreenContent] (which is what `@Preview` renders).
 */
@Composable
fun MainScreen(
    controller: PluginController,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
) {
    val state by controller.state.collectAsState()
    var callsign by remember { mutableStateOf("") }

    MainScreenContent(
        state = state,
        darkTheme = darkTheme,
        callsign = callsign,
        onCallsignChange = { callsign = it },
        onToggleTheme = onToggleTheme,
        onRefresh = controller::refreshLocation,
        onDrop = { controller.dropMarkerAtSelf(callsign) },
        onRemoveAll = controller::removeOwnMarkers,
        onDismissMessage = controller::clearMessage,
    )
}
