package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HeroBlue,
    onPrimary = CosmicBg,
    secondary = HeroBlueLight,
    onSecondary = CosmicBg,
    tertiary = HeroGold,
    onTertiary = CosmicBg,
    background = CosmicBg,
    onBackground = TextPrimary,
    surface = CosmicSurface,
    onSurface = TextPrimary,
    surfaceVariant = CosmicSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = CosmicBorder,
    error = ErrorRed
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Dark Mode as per user intent
    dynamicColor: Boolean = false, // Disable dynamic colors to preserve our hand-crafted brand theme
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
