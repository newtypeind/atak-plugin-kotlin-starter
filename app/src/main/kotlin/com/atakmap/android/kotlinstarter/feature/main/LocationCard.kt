package com.atakmap.android.kotlinstarter.feature.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atakmap.android.kotlinstarter.PluginController
import com.atakmap.android.kotlinstarter.ui.components.PreviewContainer
import com.atakmap.android.kotlinstarter.ui.components.SectionCard
import com.atakmap.android.kotlinstarter.ui.components.StatusChip
import com.atakmap.android.kotlinstarter.ui.components.StatusTone
import com.atakmap.android.kotlinstarter.ui.components.previewSelfLocation
import java.util.Locale

/**
 * Self-location card: reads the ATAK self marker (via [PluginController]) and
 * shows its MGRS grid, lat/lon, and a GPS-fix chip, with a refresh button.
 * Built from the generic [SectionCard] + [StatusChip] kit components.
 */
@Composable
fun LocationCard(
    location: PluginController.SelfLocation?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hasFix = location?.hasFix == true
    SectionCard(
        title = "My Location",
        modifier = modifier,
        icon = Icons.Filled.MyLocation,
        trailing = {
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = "Refresh location",
                    tint = MaterialTheme.colorScheme.secondary,
                )
            }
        },
    ) {
        Text(
            text = location?.mgrs ?: "Locating…",
            style = MaterialTheme.typography.headlineSmall,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = location?.let {
                String.format(Locale.US, "Lat %.6f   Lon %.6f", it.latitude, it.longitude)
            } ?: "Reading self marker…",
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))
        StatusChip(
            label = if (hasFix) "GPS FIX" else "NO FIX",
            tone = if (hasFix) StatusTone.Positive else StatusTone.Negative,
        )
    }
}

@Preview(name = "LocationCard")
@Composable
private fun LocationCardPreview() {
    PreviewContainer(darkTheme = true) {
        LocationCard(location = previewSelfLocation, onRefresh = {})
    }
}
