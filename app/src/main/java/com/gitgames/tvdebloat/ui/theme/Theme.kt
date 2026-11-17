package com.gitgames.tvdebloat.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme

object TvColors {
    val Background = Color(0xFF0B1118)
    val Surface = Color(0xFF151D27)
    val SurfaceFocused = Color(0xFF223246)
    val Accent = Color(0xFF3D8BFF)
    val TextPrimary = Color(0xFFF2F5F9)
    val TextMuted = Color(0xFF8A97A8)
}

@Composable
fun DebloatTvTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = TvColors.Accent,
            onPrimary = TvColors.TextPrimary,
            background = TvColors.Background,
            onBackground = TvColors.TextPrimary,
            surface = TvColors.Surface,
            onSurface = TvColors.TextPrimary,
        ),
        content = content,
    )
}
