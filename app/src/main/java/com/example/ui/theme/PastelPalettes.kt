package com.example.ui.theme

import androidx.compose.ui.graphics.Color

data class PastelTheme(
    val name: String,
    val primaryHex: String,
    val backgroundHex: String,
    val accentHex: String
) {
    val primaryColor: Color get() = Color(android.graphics.Color.parseColor(primaryHex))
    val backgroundColor: Color get() = Color(android.graphics.Color.parseColor(backgroundHex))
    val accentColor: Color get() = Color(android.graphics.Color.parseColor(accentHex))
}

object PastelPalettes {
    val themes = listOf(
        PastelTheme("Muted Sage", "#B8C6C3", "#F4F7F6", "#7D928E"),
        PastelTheme("Soft Blue", "#C2D2E6", "#F0F4F8", "#7D92AC"),
        PastelTheme("Pastel Peach", "#F2D5C4", "#FAF5F2", "#B38C76"),
        PastelTheme("Pastel Rose", "#F0C2C2", "#FAF2F2", "#B37E7E"),
        PastelTheme("Soft Lavender", "#E3C2F0", "#FAF2FA", "#9B76A8"),
        PastelTheme("Pastel Mint", "#C2E6D0", "#F2FAF5", "#70A183")
    )

    val defaultText = Color(0xFF2C3E43) // Deep Charcoal Slate for maximum premium readability
    val defaultSurface = Color(0xFFFFFFFF)

    fun getThemeByPrimaryHex(hex: String): PastelTheme {
        return themes.find { it.primaryHex.equals(hex, ignoreCase = true) } ?: themes[0]
    }
}
