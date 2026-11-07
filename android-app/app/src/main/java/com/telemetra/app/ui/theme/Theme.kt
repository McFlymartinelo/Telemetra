package com.telemetra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TelemetraColorScheme = darkColorScheme(
    primary = TelemetraTeal,
    onPrimary = TelemetraBackground,
    secondary = TelemetraCyan,
    background = TelemetraBackground,
    surface = TelemetraSurface,
    onBackground = TelemetraText,
    onSurface = TelemetraText,
    error = TelemetraRed,
)

@Composable
fun TelemetraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = TelemetraColorScheme,
        content = content,
    )
}
