package com.example.alphabetlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BlackColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = Color.Black,
    secondary = TextSecondary,
    background = PureBlack,
    surface = DarkSurface,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun AlphabetLauncherTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = BlackColorScheme,
        typography = Typography,
        content = content
    )
}