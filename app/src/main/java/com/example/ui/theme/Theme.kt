package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFADC6FF),
    onPrimary = Color(0xFF0E1116),
    secondary = Color(0xFFD2E4FF),
    onSecondary = Color(0xFF0E1116),
    tertiary = Color(0xFFFFB300),
    onTertiary = Color(0xFF0E1116),
    background = Color(0xFF0E1116),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF25252A),
    onSurfaceVariant = Color(0xFF909094),
    outline = Color(0x0DFFFFFF),
    error = Color(0xFFE57373)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF005691),
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF3D4758),
    onSecondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF916A00),
    onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF4F6F9),
    onBackground = Color(0xFF1E2022),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1E2022),
    surfaceVariant = Color(0xFFE9ECF0),
    onSurfaceVariant = Color(0xFF5C6066),
    outline = Color(0x1A000000),
    error = Color(0xFFC62828)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = ThemeConfig.isDarkTheme,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
