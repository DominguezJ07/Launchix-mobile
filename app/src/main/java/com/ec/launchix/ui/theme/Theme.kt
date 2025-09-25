package com.ec.launchix.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = LaunchixRed,
    onPrimary = SurfaceWhite,
    primaryContainer = LaunchixRedLight,
    secondary = LaunchixOrange,
    onSecondary = SurfaceWhite,
    secondaryContainer = LaunchixRose,
    onSecondaryContainer = Gray700,
    tertiary = LaunchixCoral,
    onTertiary = SurfaceWhite,
    tertiaryContainer = LaunchixRose,
    onTertiaryContainer = Gray700,
    background = SurfaceWhite,
    onBackground = Gray900,
    surface = SurfaceWhite,
    onSurface = Gray900,
    surfaceVariant = SurfaceLight,
    onSurfaceVariant = Gray700,
    outline = Gray300,
    outlineVariant = Gray200,
    error = ErrorRed,
    onError = SurfaceWhite,
    errorContainer = LaunchixRedLight,
    surfaceTint = LaunchixRed,
    inverseSurface = Gray800,
    inverseOnSurface = Gray100,
    inversePrimary = LaunchixRedAccent
)

@Composable
fun LaunchixTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}