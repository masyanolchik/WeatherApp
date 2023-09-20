package com.weatherapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ColorPrimary,
    primaryContainer = ColorPrimaryContainer,
    onPrimaryContainer = ColorOnPrimaryContainer,

    secondary = ColorSecondary,
    secondaryContainer = ColorSecondaryContainer,
    onSecondaryContainer = ColorOnSecondaryContainer,

    tertiary = ColorTertiary,
    onTertiary = ColorOnTertiary,
    tertiaryContainer = ColorTertiaryContainer,
    onTertiaryContainer = ColorOnTertiaryContainer,

    error = ColorError,
    onError = ColorOnError,
    onErrorContainer = ColorOnErrorContainer,
    errorContainer = ColorErrorContainer,

    background = ColorBackground,
    surface = ColorSurface,
    surfaceVariant = ColorSurfaceVariant,
    onPrimary = ColorOnPrimary,
    onSecondary = ColorOnSecondary,
    onBackground = ColorOnBackground,
    onSurface = ColorOnSurface,
    onSurfaceVariant = ColorOnSurfaceVariant,
    outline = ColorOutline
)

@Composable
fun WeatherAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}