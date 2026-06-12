package com.atakmap.android.kotlinstarter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** The semantic color of an [InlineBanner]. */
enum class BannerTone { Info, Success, Error }

/**
 * An inline message strip for feedback that should stay on screen (unlike a
 * toast): the result of an action, a hint, an error. Pass [onDismiss] to show a
 * close button.
 */
@Composable
fun InlineBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: BannerTone = BannerTone.Info,
    onDismiss: (() -> Unit)? = null,
) {
    val accent = when (tone) {
        BannerTone.Info -> MaterialTheme.colorScheme.onSurfaceVariant
        BannerTone.Success -> MaterialTheme.colorScheme.primary
        BannerTone.Error -> MaterialTheme.colorScheme.error
    }
    val icon: ImageVector = when (tone) {
        BannerTone.Info -> Icons.Filled.Info
        BannerTone.Success -> Icons.Filled.CheckCircle
        BannerTone.Error -> Icons.Filled.ErrorOutline
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = accent.copy(alpha = 0.12f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accent)
            Spacer(Modifier.width(10.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
            if (onDismiss != null) {
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Preview(name = "InlineBanner · tones")
@Composable
private fun InlineBannerPreview() {
    PreviewContainer(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InlineBanner("Dropped “Starter 1”.", tone = BannerTone.Success, onDismiss = {})
            InlineBanner("Reading the self marker…", tone = BannerTone.Info)
            InlineBanner("No self position yet.", tone = BannerTone.Error)
        }
    }
}
