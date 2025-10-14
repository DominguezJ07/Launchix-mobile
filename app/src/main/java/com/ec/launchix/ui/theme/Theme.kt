package com.ec.launchix.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFDC040),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE5A0),
    onPrimaryContainer = Color(0xFF1F1B00),

    secondary = Color(0xFFFFB800),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFE0A0),
    onSecondaryContainer = Color(0xFF2B1700),

    tertiary = Color(0xFF825500),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDDB3),
    onTertiaryContainer = Color(0xFF2A1800),

    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),

    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1F1B16),

    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1F1B16),
    surfaceVariant = Color(0xFFEBE1CF),
    onSurfaceVariant = Color(0xFF4C4639),

    outline = Color(0xFF7D7667),
    inverseOnSurface = Color(0xFFF8EFE7),
    inverseSurface = Color(0xFF34302A),
    inversePrimary = Color(0xFFFDC040),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFDC040),
    onPrimary = Color(0xFF3E2E00),
    primaryContainer = Color(0xFF5A4300),
    onPrimaryContainer = Color(0xFFFFE5A0),

    secondary = Color(0xFFFFB800),
    onSecondary = Color(0xFF442B00),
    secondaryContainer = Color(0xFF633F00),
    onSecondaryContainer = Color(0xFFFFE0A0),

    tertiary = Color(0xFFFFB951),
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF633F00),
    onTertiaryContainer = Color(0xFFFFDDB3),

    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFFBB0006),
    onErrorContainer = Color(0xFFFFDAD6),

    background = Color(0xFF000000),
    onBackground = Color(0xFFEAE1D9),

    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFEAE1D9),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFCFC5B4),

    outline = Color(0xFF998F80),
    inverseOnSurface = Color(0xFF000000),
    inverseSurface = Color(0xFFEAE1D9),
    inversePrimary = Color(0xFF785900),
)

@Composable
fun LaunchixTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}