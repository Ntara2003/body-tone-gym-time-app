package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BodyToneRed,
    onPrimary = Color.White,
    primaryContainer = BodyToneRedDark,
    onPrimaryContainer = Color.White,
    secondary = RestCyan,
    onSecondary = Color.Black,
    tertiary = GoldChamp,
    onTertiary = Color.Black,
    background = GymBlack,
    onBackground = Color.White,
    surface = GymSurface,
    onSurface = Color.White,
    surfaceVariant = GymSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = BorderDark
)

@Composable
fun BodyToneBoxingTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
