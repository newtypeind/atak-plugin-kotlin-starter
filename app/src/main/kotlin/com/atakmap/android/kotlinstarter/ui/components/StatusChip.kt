package com.atakmap.android.kotlinstarter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** The semantic color of a [StatusChip]. */
enum class StatusTone { Positive, Negative, Neutral }

/**
 * A small pill with a colored dot and a label, for status indicators
 * ("GPS FIX" / "OFFLINE" / "ARMED"). Pick a [tone] for the color.
 */
@Composable
fun StatusChip(label: String, tone: StatusTone = StatusTone.Neutral, modifier: Modifier = Modifier) {
    val color = when (tone) {
        StatusTone.Positive -> MaterialTheme.colorScheme.primary
        StatusTone.Negative -> MaterialTheme.colorScheme.error
        StatusTone.Neutral -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(modifier = modifier, shape = CircleShape, color = color.copy(alpha = 0.16f)) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}

@Preview(name = "StatusChip · tones")
@Composable
private fun StatusChipPreview() {
    PreviewContainer(darkTheme = true) {
        Row {
            StatusChip("GPS FIX", StatusTone.Positive)
            Spacer(Modifier.width(8.dp))
            StatusChip("NO FIX", StatusTone.Negative)
            Spacer(Modifier.width(8.dp))
            StatusChip("IDLE", StatusTone.Neutral)
        }
    }
}
