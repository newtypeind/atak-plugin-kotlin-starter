package com.atakmap.android.kotlinstarter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * The three buttons most plugin screens need, as thin Material 3 wrappers so a
 * screen reads as intent (primary / secondary / destructive) rather than styling.
 * Each takes an optional leading [icon]. Pass `Modifier.fillMaxWidth()` for a
 * full-width call-to-action.
 */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(onClick = onClick, modifier = modifier, enabled = enabled) { ButtonContent(text, icon) }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled) {
        ButtonContent(text, icon)
    }
}

@Composable
fun DestructiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error,
            contentColor = MaterialTheme.colorScheme.onError,
        ),
    ) { ButtonContent(text, icon) }
}

@Composable
private fun RowScope.ButtonContent(text: String, icon: ImageVector?) {
    if (icon != null) {
        Icon(imageVector = icon, contentDescription = null)
        Spacer(Modifier.width(8.dp))
    }
    Text(text)
}

@Preview(name = "Buttons")
@Composable
private fun ButtonsPreview() {
    PreviewContainer(darkTheme = true) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            PrimaryButton("Primary", {})
            SecondaryButton("Secondary", {})
            DestructiveButton("Delete", {})
        }
    }
}
