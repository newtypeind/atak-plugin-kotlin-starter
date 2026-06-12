package com.atakmap.android.kotlinstarter.compose

import androidx.compose.ui.graphics.Color

/**
 * Color tokens for a shadcn-style neutral palette, expressed as sRGB
 * approximations of the original OKLCH values.
 *
 * Mapping note: the source palette uses {background, foreground, card,
 * primary, secondary, muted, accent, border, destructive}. Material 3 has a
 * wider vocabulary (containers + onColors). Each token is snapped onto the
 * closest Material 3 slot so `MaterialTheme.colorScheme.*` stays the single
 * source of truth in UI code.
 */

// --- Light mode -------------------------------------------------------------
val LightBackground = Color(0xFFFFFFFF)        // oklch(1 0 0)
val LightForeground = Color(0xFF252525)        // oklch(0.145 0 0)
val LightCard = Color(0xFFFFFFFF)              // oklch(1 0 0)
val LightPrimary = Color(0xFF353535)           // oklch(0.205 0 0) — near-black action surface
val LightPrimaryForeground = Color(0xFFFAFAFA) // oklch(0.985 0 0)
val LightMutedSurface = Color(0xFFF7F7F7)      // oklch(0.97 0 0) — secondary / muted / accent
val LightMutedForeground = Color(0xFF8E8E8E)   // oklch(0.556 0 0)
val LightBorder = Color(0xFFD4D4D4)            // oklch(0.85 0 0) — deepened for divider contrast on white
val LightDestructive = Color(0xFFD04C4C)       // oklch(0.577 0.245 27.325)

// --- Dark mode --------------------------------------------------------------
val DarkBackground = Color(0xFF252525)         // oklch(0.145 0 0)
val DarkForeground = Color(0xFFFAFAFA)         // oklch(0.985 0 0)
val DarkCard = Color(0xFF353535)               // oklch(0.205 0 0) — surface variant
val DarkPrimary = Color(0xFFEBEBEB)            // oklch(0.922 0 0) — inverted: light action surface
val DarkPrimaryForeground = Color(0xFF353535)  // oklch(0.205 0 0)
val DarkMutedSurface = Color(0xFF444444)       // oklch(0.269 0 0)
val DarkMutedForeground = Color(0xFFB4B4B4)    // oklch(0.708 0 0)
val DarkBorder = Color(0xFF444444)             // oklch(0.269 0 0) — same as muted
val DarkDestructive = Color(0xFFE36868)        // brighter red so it pops on dark
