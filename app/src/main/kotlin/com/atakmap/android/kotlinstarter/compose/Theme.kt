package com.atakmap.android.kotlinstarter.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

/**
 * Light / dark color schemes built from a shadcn-style neutral palette (see
 * [Color.kt]). Material You dynamic color is deliberately off: the palette is
 * fixed, and inheriting the system wallpaper hue would diverge from it.
 */
private val LightColorScheme = lightColorScheme(
    background = LightBackground,
    onBackground = LightForeground,
    surface = LightCard,
    onSurface = LightForeground,
    surfaceVariant = LightMutedSurface,
    onSurfaceVariant = LightMutedForeground,
    primary = LightPrimary,
    onPrimary = LightPrimaryForeground,
    primaryContainer = LightMutedSurface,
    onPrimaryContainer = LightForeground,
    secondary = LightPrimary,
    onSecondary = LightPrimaryForeground,
    secondaryContainer = LightMutedSurface,
    onSecondaryContainer = LightForeground,
    tertiary = LightPrimary,
    onTertiary = LightPrimaryForeground,
    error = LightDestructive,
    onError = LightPrimaryForeground,
    outline = LightBorder,
    outlineVariant = LightBorder,
)

private val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkForeground,
    surface = DarkCard,
    onSurface = DarkForeground,
    surfaceVariant = DarkMutedSurface,
    onSurfaceVariant = DarkMutedForeground,
    primary = DarkPrimary,
    onPrimary = DarkPrimaryForeground,
    primaryContainer = DarkMutedSurface,
    onPrimaryContainer = DarkForeground,
    secondary = DarkPrimary,
    onSecondary = DarkPrimaryForeground,
    secondaryContainer = DarkMutedSurface,
    onSecondaryContainer = DarkForeground,
    tertiary = DarkPrimary,
    onTertiary = DarkPrimaryForeground,
    error = DarkDestructive,
    onError = DarkPrimaryForeground,
    outline = DarkBorder,
    outlineVariant = DarkBorder,
)

/**
 * Root theme. [darkTheme] is hoisted (see [PluginController.isDarkTheme]) so a
 * toggle button can flip it at runtime; switching cross-fades every populated
 * scheme slot via [animateColorScheme] instead of snapping in one frame.
 *
 * Unlike a standalone app, this runs inside ATAK's window, so it does not touch
 * system bars or use `enableEdgeToEdge()`.
 */
@Composable
fun AppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    val target = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = animateColorScheme(target),
        typography = Typography,
        content = content,
    )
}

/**
 * Cross-fades every populated [ColorScheme] slot between its current value and
 * [target]. Without this, flipping the theme replaces the whole scheme in a
 * single frame and the screen snaps from dark to light, which is jarring.
 */
@Composable
private fun animateColorScheme(target: ColorScheme): ColorScheme {
    val spec: AnimationSpec<Color> = tween(
        durationMillis = THEME_TRANSITION_MS,
        easing = FastOutSlowInEasing,
    )
    val background by animateColorAsState(target.background, spec, label = "background")
    val onBackground by animateColorAsState(target.onBackground, spec, label = "onBackground")
    val surface by animateColorAsState(target.surface, spec, label = "surface")
    val onSurface by animateColorAsState(target.onSurface, spec, label = "onSurface")
    val surfaceVariant by animateColorAsState(target.surfaceVariant, spec, label = "surfaceVariant")
    val onSurfaceVariant by animateColorAsState(
        target.onSurfaceVariant, spec, label = "onSurfaceVariant",
    )
    val primary by animateColorAsState(target.primary, spec, label = "primary")
    val onPrimary by animateColorAsState(target.onPrimary, spec, label = "onPrimary")
    val primaryContainer by animateColorAsState(
        target.primaryContainer, spec, label = "primaryContainer",
    )
    val onPrimaryContainer by animateColorAsState(
        target.onPrimaryContainer, spec, label = "onPrimaryContainer",
    )
    val secondary by animateColorAsState(target.secondary, spec, label = "secondary")
    val onSecondary by animateColorAsState(target.onSecondary, spec, label = "onSecondary")
    val secondaryContainer by animateColorAsState(
        target.secondaryContainer, spec, label = "secondaryContainer",
    )
    val onSecondaryContainer by animateColorAsState(
        target.onSecondaryContainer, spec, label = "onSecondaryContainer",
    )
    val tertiary by animateColorAsState(target.tertiary, spec, label = "tertiary")
    val onTertiary by animateColorAsState(target.onTertiary, spec, label = "onTertiary")
    val error by animateColorAsState(target.error, spec, label = "error")
    val onError by animateColorAsState(target.onError, spec, label = "onError")
    val outline by animateColorAsState(target.outline, spec, label = "outline")
    val outlineVariant by animateColorAsState(target.outlineVariant, spec, label = "outlineVariant")

    return target.copy(
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        tertiary = tertiary,
        onTertiary = onTertiary,
        error = error,
        onError = onError,
        outline = outline,
        outlineVariant = outlineVariant,
    )
}

private const val THEME_TRANSITION_MS = 500
