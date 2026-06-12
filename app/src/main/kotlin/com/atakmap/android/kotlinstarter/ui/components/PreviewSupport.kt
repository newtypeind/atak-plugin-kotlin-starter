package com.atakmap.android.kotlinstarter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.atakmap.android.kotlinstarter.PluginController
import com.atakmap.android.kotlinstarter.compose.AppTheme

/**
 * Shared `@Preview` scaffolding so every component's preview stays a one-liner:
 * a themed, padded container plus sample state. Design-time only (never runs on
 * device).
 */

internal val previewSelfLocation = PluginController.SelfLocation(
    latitude = 37.5665,
    longitude = 126.9780,
    mgrs = "52S CG 02468 13579",
    hasFix = true,
)

internal val previewUiState = PluginController.UiState(
    location = previewSelfLocation,
    markerCount = 2,
    message = "Dropped “Starter 2”.",
)

/** Wraps preview content in [AppTheme] + a padded background of the chosen theme. */
@Composable
internal fun PreviewContainer(darkTheme: Boolean, content: @Composable () -> Unit) {
    AppTheme(darkTheme = darkTheme) {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(Modifier.padding(16.dp)) { content() }
        }
    }
}
