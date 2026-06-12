package com.atakmap.android.kotlinstarter.feature.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.atakmap.android.kotlinstarter.ui.components.PreviewContainer

/** Screen title + subtitle with the light/dark theme toggle on the right. */
@Composable
fun AppHeader(
    title: String,
    subtitle: String,
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onToggleTheme) {
            Icon(
                imageVector = if (darkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
                contentDescription = if (darkTheme) "Switch to light mode" else "Switch to dark mode",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Preview(name = "AppHeader")
@Composable
private fun AppHeaderPreview() {
    PreviewContainer(darkTheme = true) {
        AppHeader(
            title = "Kotlin Compose Starter",
            subtitle = "Compose UI running inside ATAK",
            darkTheme = true,
            onToggleTheme = {},
        )
    }
}
