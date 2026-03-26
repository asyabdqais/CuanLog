package com.example.finrecapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    background = DarkBg,
    surface = CardBg,
    onPrimary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    background = Color(0xFFF8F9FA),
    surface = Color.White,
    onPrimary = Color.White,
    onBackground = Color(0xFF1A1C29),
    onSurface = Color(0xFF1A1C29)
)

@Composable
fun FinRecTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
