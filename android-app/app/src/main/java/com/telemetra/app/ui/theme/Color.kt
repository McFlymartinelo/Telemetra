package com.telemetra.app.ui.theme

import androidx.compose.ui.graphics.Color

val TelemetraBackground = Color(0xFF0D1B2E)
val TelemetraSurface = Color(0xFF1A2A40)
val TelemetraTeal = Color(0xFF2DD4BF)
val TelemetraTealDim = Color(0xFF1A8A7A)
val TelemetraText = Color(0xFFFFFFFF)
val TelemetraTextDim = Color(0xFF8BA4C0)
val TelemetraGreen = Color(0xFF22C55E)
val TelemetraAmber = Color(0xFFF59E0B)
val TelemetraRed = Color(0xFFEF4444)
val TelemetraPurple = Color(0xFFA855F7)
val TelemetraCyan = Color(0xFF06B6D4)
val TelemetraBlue = Color(0xFF3B82F6)

fun tireTempColor(tempC: Float): Color = when {
    tempC < 30f -> TelemetraBlue
    tempC < 50f -> TelemetraGreen
    tempC < 70f -> TelemetraAmber
    else -> TelemetraRed
}

fun deltaColor(delta: Float): Color = when {
    delta < -0.1f -> TelemetraGreen
    delta > 0.1f -> TelemetraRed
    else -> TelemetraTextDim
}
