package com.atakmap.android.kotlinstarter

import android.content.Context
import com.atakmap.android.kotlinstarter.util.L
import com.atakmap.android.maps.MapView
import com.atakmap.android.maps.Marker
import com.atakmap.android.user.PlacePointTool
import com.atakmap.coremap.conversions.CoordinateFormat
import com.atakmap.coremap.conversions.CoordinateFormatUtilities
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Holds the plugin's screen state ([UiState]) as a [StateFlow] and is the single
 * place that talks to ATAK: it reads the self marker's position and drops/removes
 * CoT markers on the map. Keeping all ATAK access here lets the whole Compose
 * layer above stay pure data-in / callbacks-out, so every screen renders in
 * `@Preview` without a live ATAK.
 *
 * @param context ATAK's MapView context, used for the theme [android.content.SharedPreferences].
 */
class PluginController(private val context: Context) {

    /** Self position, formatted for display. */
    data class SelfLocation(
        val latitude: Double,
        val longitude: Double,
        val mgrs: String,
        val hasFix: Boolean,
    )

    /** Everything the main screen renders. Plain data so it is `@Preview`-able. */
    data class UiState(
        val location: SelfLocation? = null,
        val markerCount: Int = 0,
        val message: String? = null,
    )

    private val _state = MutableStateFlow(UiState())
    val state: StateFlow<UiState> = _state.asStateFlow()

    // Theme mode (dark by default, since ATAK runs a dark map UI), persisted so the
    // choice survives the DropDown being closed and reopened. Hoisted into the UI
    // by [com.atakmap.android.kotlinstarter.ui.PluginApp]; the header toggle calls
    // [toggleTheme].
    private val themePrefs = context.getSharedPreferences("plugin.theme", Context.MODE_PRIVATE)
    private val _isDarkTheme = MutableStateFlow(themePrefs.getBoolean(KEY_DARK_THEME, true))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Markers this plugin placed this session, so "Remove markers" clears only its
    // own and not the user's other map items.
    private val ownMarkers = mutableListOf<Marker>()

    fun toggleTheme() {
        val next = !_isDarkTheme.value
        _isDarkTheme.value = next
        themePrefs.edit().putBoolean(KEY_DARK_THEME, next).apply()
    }

    /** Reads the self marker's position into [UiState.location]. */
    fun refreshLocation() {
        val point = MapView.getMapView()?.selfMarker?.point
        if (point == null) {
            _state.update { it.copy(location = SelfLocation(0.0, 0.0, "NO SELF MARKER", false)) }
            L.d("refreshLocation: no self marker")
            return
        }
        val mgrs = runCatching {
            CoordinateFormatUtilities.formatToString(point, CoordinateFormat.MGRS)
        }.getOrDefault("—")
        val hasFix = point.isValid && (point.latitude != 0.0 || point.longitude != 0.0)
        _state.update {
            it.copy(location = SelfLocation(point.latitude, point.longitude, mgrs, hasFix))
        }
        L.d("refreshLocation: $mgrs fix=$hasFix")
    }

    /**
     * Drops a CoT marker at the self marker's position with ATAK's
     * [PlacePointTool.MarkerCreator]. The result is an ordinary map item: tap it on
     * the map, find it in Overlay Manager, and (if connected) it shares over the
     * network like any other marker.
     */
    fun dropMarkerAtSelf(callsign: String) {
        val point = MapView.getMapView()?.selfMarker?.point
        // A self marker with no GPS fix sits at 0,0 (a valid GeoPoint), so guard on
        // a real position, not just point.isValid - otherwise the marker lands in
        // the Gulf of Guinea and the "dropped" message would be misleading.
        if (point == null || !point.isValid || (point.latitude == 0.0 && point.longitude == 0.0)) {
            _state.update { it.copy(message = "No self position yet — wait for a GPS fix.") }
            return
        }
        val name = callsign.trim().ifBlank { "Starter ${ownMarkers.size + 1}" }
        // 2525 type a-f-G-U-C = friendly ground unit (a blue icon). Change the type
        // string to place a different symbol.
        val marker = PlacePointTool.MarkerCreator(point)
            .setType("a-f-G-U-C")
            .setCallsign(name)
            .showCotDetails(false)
            .placePoint()
        ownMarkers += marker
        _state.update { it.copy(markerCount = ownMarkers.size, message = "Dropped “$name”.") }
        L.d("dropMarkerAtSelf: $name @ ${point.latitude},${point.longitude}")
    }

    /** Removes every marker this plugin placed this session. */
    fun removeOwnMarkers() {
        if (ownMarkers.isEmpty()) {
            _state.update { it.copy(message = "No markers to remove.") }
            return
        }
        val n = ownMarkers.size
        ownMarkers.forEach { it.removeFromGroup() }
        ownMarkers.clear()
        _state.update { it.copy(markerCount = 0, message = "Removed $n marker(s).") }
        L.d("removeOwnMarkers: $n")
    }

    /** Clears the transient status message after the UI has shown it. */
    fun clearMessage() = _state.update { it.copy(message = null) }

    fun release() {
        // Dropped markers are normal map items, so they stay on the map; just drop
        // our references to them.
        ownMarkers.clear()
    }

    private companion object {
        const val KEY_DARK_THEME = "dark_theme"
    }
}
