package com.atakmap.android.kotlinstarter.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atakmap.android.kotlinstarter.PluginController
import com.atakmap.android.kotlinstarter.compose.AppTheme
import com.atakmap.android.kotlinstarter.ui.components.BannerTone
import com.atakmap.android.kotlinstarter.ui.components.InlineBanner
import com.atakmap.android.kotlinstarter.ui.components.previewUiState

/**
 * Stateless body of [MainScreen]: header, an optional status banner, the
 * self-location card, and the marker example, stacked in a scrolling column.
 * Everything is passed in as plain state + callbacks, so this whole screen
 * renders in `@Preview` with no live [PluginController] (which needs ATAK).
 */
@Composable
fun MainScreenContent(
    state: PluginController.UiState,
    darkTheme: Boolean,
    callsign: String,
    onCallsignChange: (String) -> Unit,
    onToggleTheme: () -> Unit,
    onRefresh: () -> Unit,
    onDrop: () -> Unit,
    onRemoveAll: () -> Unit,
    onDismissMessage: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            AppHeader(
                title = "Kotlin Compose Starter",
                subtitle = "Compose UI running inside ATAK",
                darkTheme = darkTheme,
                onToggleTheme = onToggleTheme,
            )
            state.message?.let { message ->
                InlineBanner(message = message, tone = BannerTone.Info, onDismiss = onDismissMessage)
            }
            LocationCard(location = state.location, onRefresh = onRefresh)
            MarkerCard(
                markerCount = state.markerCount,
                callsign = callsign,
                onCallsignChange = onCallsignChange,
                onDrop = onDrop,
                onRemoveAll = onRemoveAll,
            )
        }
    }
}

@Preview(name = "MainScreen · dark", showBackground = true, heightDp = 820)
@Composable
private fun MainScreenDarkPreview() {
    AppTheme(darkTheme = true) {
        MainScreenContent(
            state = previewUiState,
            darkTheme = true,
            callsign = "",
            onCallsignChange = {},
            onToggleTheme = {},
            onRefresh = {},
            onDrop = {},
            onRemoveAll = {},
            onDismissMessage = {},
        )
    }
}

@Preview(name = "MainScreen · light", showBackground = true, heightDp = 820)
@Composable
private fun MainScreenLightPreview() {
    AppTheme(darkTheme = false) {
        MainScreenContent(
            state = previewUiState,
            darkTheme = false,
            callsign = "",
            onCallsignChange = {},
            onToggleTheme = {},
            onRefresh = {},
            onDrop = {},
            onRemoveAll = {},
            onDismissMessage = {},
        )
    }
}
