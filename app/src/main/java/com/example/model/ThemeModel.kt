package com.example.model

import androidx.compose.ui.graphics.Color

enum class AppTheme(
    val id: String,
    val displayName: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val backgroundGradient: List<Color>,
    val glassBackground: Color,
    val glassBorder: Color,
    val highlightColor: Color
) {
    AQUA_GLASS(
        id = "aqua_glass",
        displayName = "Aqua Glass",
        primaryAccent = Color(0xFF00F0FF),
        secondaryAccent = Color(0xFF0284C7),
        backgroundGradient = listOf(
            Color(0xFF061A3A),
            Color(0xFF052A4A),
            Color(0xFF031628),
            Color(0xFF020C17)
        ),
        glassBackground = Color(0xFF0B2447).copy(alpha = 0.72f),
        glassBorder = Color(0xFF38BDF8).copy(alpha = 0.45f),
        highlightColor = Color(0xFF67E8F9)
    ),
    CYBER_NEON(
        id = "cyber_neon",
        displayName = "Cyber Neon",
        primaryAccent = Color(0xFF8B5CF6),
        secondaryAccent = Color(0xFFEC4899),
        backgroundGradient = listOf(
            Color(0xFF1E0836),
            Color(0xFF130424),
            Color(0xFF08010F)
        ),
        glassBackground = Color(0xFF220A3C).copy(alpha = 0.75f),
        glassBorder = Color(0xFFA855F7).copy(alpha = 0.35f),
        highlightColor = Color(0xFFF472B6)
    ),
    SUNSET_CORAL(
        id = "sunset_coral",
        displayName = "Sunset Coral",
        primaryAccent = Color(0xFFFB923C),
        secondaryAccent = Color(0xFFF43F5E),
        backgroundGradient = listOf(
            Color(0xFF2E1022),
            Color(0xFF1A0A16),
            Color(0xFF0C040B)
        ),
        glassBackground = Color(0xFF331427).copy(alpha = 0.75f),
        glassBorder = Color(0xFFFB7185).copy(alpha = 0.35f),
        highlightColor = Color(0xFFFDBA74)
    ),
    OLED_BLACK(
        id = "oled_black",
        displayName = "OLED Pure",
        primaryAccent = Color(0xFF38BDF8),
        secondaryAccent = Color(0xFF64748B),
        backgroundGradient = listOf(
            Color(0xFF000000),
            Color(0xFF070B12),
            Color(0xFF000000)
        ),
        glassBackground = Color(0xFF0F172A).copy(alpha = 0.70f),
        glassBorder = Color.White.copy(alpha = 0.15f),
        highlightColor = Color(0xFF94A3B8)
    );

    companion object {
        fun fromId(id: String): AppTheme {
            return entries.find { it.id == id } ?: AQUA_GLASS
        }
    }
}
