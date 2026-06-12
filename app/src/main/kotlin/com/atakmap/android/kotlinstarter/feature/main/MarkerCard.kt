package com.atakmap.android.kotlinstarter.feature.main

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddLocationAlt
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atakmap.android.kotlinstarter.ui.components.DestructiveButton
import com.atakmap.android.kotlinstarter.ui.components.LabeledTextField
import com.atakmap.android.kotlinstarter.ui.components.PreviewContainer
import com.atakmap.android.kotlinstarter.ui.components.PrimaryButton
import com.atakmap.android.kotlinstarter.ui.components.SectionCard
import com.atakmap.android.kotlinstarter.ui.components.StatusChip
import com.atakmap.android.kotlinstarter.ui.components.StatusTone

/**
 * The "drop a marker on the map" example: a callsign field, a button that places
 * a CoT marker at the self position, and a destructive remove. Shows how Compose
 * state drives a real ATAK map operation (see
 * [com.atakmap.android.kotlinstarter.PluginController.dropMarkerAtSelf]).
 *
 * [onRemoveRequested] asks the screen to open the confirm dialog; the dialog
 * itself is hosted at the screen root (see [MainScreenContent]) because an
 * in-pane modal must overlay the whole pane, not sit inside this card's column.
 */
@Composable
fun MarkerCard(
    markerCount: Int,
    callsign: String,
    onCallsignChange: (String) -> Unit,
    onDrop: () -> Unit,
    onRemoveRequested: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SectionCard(
        title = "Map markers",
        modifier = modifier,
        icon = Icons.Filled.AddLocationAlt,
        trailing = { StatusChip("$markerCount placed", StatusTone.Neutral) },
    ) {
        LabeledTextField(
            value = callsign,
            onValueChange = onCallsignChange,
            label = "Callsign",
            placeholder = "Auto (Starter N)",
        )
        Spacer(Modifier.height(14.dp))
        PrimaryButton(
            text = "Drop at my location",
            onClick = onDrop,
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.Place,
        )
        Spacer(Modifier.height(8.dp))
        DestructiveButton(
            text = "Remove my markers",
            onClick = onRemoveRequested,
            modifier = Modifier.fillMaxWidth(),
            enabled = markerCount > 0,
            icon = Icons.Filled.DeleteOutline,
        )
    }
}

@Preview(name = "MarkerCard")
@Composable
private fun MarkerCardPreview() {
    PreviewContainer(darkTheme = true) {
        var callsign by remember { mutableStateOf("") }
        MarkerCard(
            markerCount = 2,
            callsign = callsign,
            onCallsignChange = { callsign = it },
            onDrop = {},
            onRemoveRequested = {},
        )
    }
}
