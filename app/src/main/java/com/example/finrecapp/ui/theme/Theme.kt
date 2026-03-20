package com.example.finrecapp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrange,
    background = DarkBg,
    surface = CardBg,
    onPrimary = White,
    onBackground = White,
    onSurface = White
)

@Composable
fun FinRecTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
