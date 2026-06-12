package com.atakmap.android.kotlinstarter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A single-select segmented control. Generic over the option type [T]; [label]
 * maps each option to its button text. Good for switching a screen between a few
 * modes without the weight of a TabRow.
 */
@Composable
fun <T> SegmentedTabs(
    options: List<T>,
    selected: T,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
    label: (T) -> String = { it.toString() },
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(modifier = Modifier.padding(4.dp)) {
            options.forEach { option ->
                val isSelected = option == selected
                Surface(
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(9.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    contentColor = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label(option),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "SegmentedTabs")
@Composable
private fun SegmentedTabsPreview() {
    PreviewContainer(darkTheme = true) {
        var selected by remember { mutableStateOf("Map") }
        SegmentedTabs(
            options = listOf("Map", "List", "Settings"),
            selected = selected,
            onSelect = { selected = it },
        )
    }
}
