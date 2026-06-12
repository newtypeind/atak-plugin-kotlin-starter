package com.atakmap.android.kotlinstarter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A yes/no confirmation modal. Set [destructive] to tint the confirm action red
 * (delete, remove, disconnect).
 *
 * It is NOT built on Material's [androidx.compose.material3.AlertDialog]. A Compose
 * `Dialog`/`Popup` opens a brand-new platform window, and inside an ATAK plugin
 * that window has no valid Activity token, so it crashes with
 * `BadTokenException: token null is not valid`. Instead this renders an in-pane
 * overlay (a scrim + a card) inside the existing ComposeView, which is already
 * validly attached. Render it as the last child of a `Box(Modifier.fillMaxSize())`
 * (e.g. your screen root), guarded by your "is open" state, so it covers the pane.
 */
@Composable
fun ConfirmDialog(
    title: String,
    text: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmLabel: String = "Confirm",
    dismissLabel: String = "Cancel",
    destructive: Boolean = false,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            // Tap on the scrim dismisses.
            .pointerInput(Unit) { detectTapGestures { onDismiss() } },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 360.dp)
                // Swallow taps on the card so they do not reach the scrim.
                .pointerInput(Unit) { detectTapGestures {} },
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text(dismissLabel) }
                    Spacer(Modifier.width(8.dp))
                    TextButton(onClick = onConfirm) {
                        Text(
                            text = confirmLabel,
                            color = if (destructive) {
                                MaterialTheme.colorScheme.error
                            } else {
                                Color.Unspecified
                            },
                        )
                    }
                }
            }
        }
    }
}

@Preview(name = "ConfirmDialog")
@Composable
private fun ConfirmDialogPreview() {
    PreviewContainer(darkTheme = true) {
        Box(Modifier.fillMaxSize()) {
            ConfirmDialog(
                title = "Remove markers",
                text = "Remove the 2 marker(s) this plugin placed?",
                confirmLabel = "Remove",
                destructive = true,
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}
