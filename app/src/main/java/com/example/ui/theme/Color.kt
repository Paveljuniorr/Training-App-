package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

// Global theme manager that triggers Compose recomposition when toggled
object ThemeConfig {
    var isDarkTheme by mutableStateOf(true)
}

// Sophisticated Dynamic Colors (Light and Dark Mode support)
val HeroBlue: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFADC6FF) else Color(0xFF005691)
val HeroBlueLight: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFD2E4FF) else Color(0xFF5CC0FF)
val HeroBlueDark: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF3D4758) else Color(0xFF003054)
val HeroGold: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFFFB300) else Color(0xFF916A00)
val HeroGoldLight: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFFFDF7A) else Color(0xFFE5A900)

// Background surfaces
val CosmicBg: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF0E1116) else Color(0xFFF4F6F9)
val CosmicSurface: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF1B1B1F) else Color(0xFFFFFFFF)
val CosmicSurfaceVariant: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF25252A) else Color(0xFFE9ECF0)
val CosmicBorder: Color get() = if (ThemeConfig.isDarkTheme) Color(0x0DFFFFFF) else Color(0x1A000000)

// Text Colors
val TextPrimary: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFE2E2E6) else Color(0xFF1E2022)
val TextSecondary: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF909094) else Color(0xFF5C6066)
val TextMuted: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF656569) else Color(0xFF8E9094)
val SuccessGreen: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFF8EAA8A) else Color(0xFF2E7D32)
val WarningOrange: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFDDA15E) else Color(0xFFEF6C00)
val ErrorRed: Color get() = if (ThemeConfig.isDarkTheme) Color(0xFFE57373) else Color(0xFFC62828)


