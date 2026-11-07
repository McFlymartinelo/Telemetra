package com.telemetra.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemetra.app.data.model.TireTelemetry
import com.telemetra.app.ui.theme.TelemetraSurface
import com.telemetra.app.ui.theme.TelemetraTeal
import com.telemetra.app.ui.theme.TelemetraText
import com.telemetra.app.ui.theme.TelemetraTextDim
import com.telemetra.app.ui.theme.deltaColor
import com.telemetra.app.ui.theme.tireTempColor

@Composable
fun GaugeStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TelemetraText,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(TelemetraSurface.copy(alpha = 0.55f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            color = TelemetraTextDim,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            value,
            color = valueColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun GaugeTireCard(
    tire: TireTelemetry,
    modifier: Modifier = Modifier,
) {
    val borderColor = tireTempColor(tire.tempC)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .background(TelemetraSurface.copy(alpha = 0.5f))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            tire.position.name,
            color = TelemetraTextDim,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(28.dp, 38.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(borderColor.copy(alpha = 0.15f))
                .border(1.5.dp, borderColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(borderColor),
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "${tire.tempC.toInt()}°C",
            color = borderColor,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun CompactStatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TelemetraText,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(TelemetraSurface.copy(alpha = 0.55f))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            color = TelemetraTextDim,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun CompactTireCard(
    tire: TireTelemetry,
    modifier: Modifier = Modifier,
) {
    val borderColor = tireTempColor(tire.tempC)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(8.dp))
            .background(TelemetraSurface.copy(alpha = 0.55f))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(tire.position.name, color = TelemetraTextDim, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        Box(
            modifier = Modifier
                .padding(vertical = 2.dp)
                .size(18.dp, 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(borderColor.copy(alpha = 0.2f))
                .border(1.dp, borderColor, RoundedCornerShape(4.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(borderColor),
            )
        }
        Text("${tire.tempC.toInt()}°C", color = borderColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TelemetraText,
) {
    Column(
        modifier = modifier
            .defaultMinSize(minWidth = 100.dp, minHeight = 58.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(TelemetraSurface.copy(alpha = 0.55f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, color = TelemetraTextDim, fontSize = 11.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            value,
            color = valueColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun TealCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = TelemetraText,
    large: Boolean = false,
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = if (large) 120.dp else 72.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, TelemetraTeal.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        TelemetraSurface.copy(alpha = 0.65f),
                        TelemetraSurface.copy(alpha = 0.35f),
                    ),
                ),
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            label,
            color = TelemetraTextDim,
            fontSize = if (large) 12.sp else 10.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = valueColor,
            fontSize = if (large) 32.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun TireCardGauge(
    tire: TireTelemetry,
    modifier: Modifier = Modifier,
) {
    val borderColor = tireTempColor(tire.tempC)
    Column(
        modifier = modifier
            .defaultMinSize(minWidth = 88.dp, minHeight = 110.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .background(TelemetraSurface.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(tire.position.name, color = TelemetraTextDim, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        TireIcon(color = borderColor)
        Spacer(modifier = Modifier.height(6.dp))
        Text("${tire.tempC.toInt()}°C", color = borderColor, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text("${tire.wearPct.toInt()}%", color = TelemetraTextDim, fontSize = 11.sp)
    }
}

@Composable
fun TireCardSimple(
    tire: TireTelemetry,
    modifier: Modifier = Modifier,
) {
    val borderColor = tireTempColor(tire.tempC)
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 88.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
            .background(TelemetraSurface.copy(alpha = 0.45f))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(tire.position.name, color = TelemetraTextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("${tire.tempC.toInt()}°C", color = borderColor, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text("${tire.wearPct.toInt()}% usure", color = TelemetraTextDim, fontSize = 11.sp)
    }
}

@Composable
private fun TireIcon(color: Color) {
    Box(
        modifier = Modifier
            .size(32.dp, 42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.verticalGradient(
                    listOf(color.copy(alpha = 0.35f), color.copy(alpha = 0.12f)),
                ),
            )
            .border(1.5.dp, color.copy(alpha = 0.8f), RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(14.dp)
                .clip(RoundedCornerShape(50))
                .background(color),
        )
    }
}

@Composable
fun LapDeltaDisplay(
    delta: Float,
    modifier: Modifier = Modifier,
    fontSize: Int = 22,
) {
    val sign = if (delta >= 0) "+" else ""
    val color = deltaColor(delta)
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(
            if (delta < 0) "▲" else if (delta > 0) "▼" else "—",
            color = color,
            fontSize = (fontSize - 6).sp,
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            "$sign${"%.3f".format(delta)}s",
            color = color,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun SpeedDisplay(
    speedKmh: Float,
    useMph: Boolean,
    modifier: Modifier = Modifier,
    large: Boolean = true,
    inline: Boolean = false,
) {
    val speed = if (useMph) speedKmh * 0.621371f else speedKmh
    val unit = if (useMph) "mph" else "km/h"

    if (inline) {
        Row(modifier = modifier, verticalAlignment = Alignment.Bottom) {
            Text(
                speed.toInt().toString(),
                color = TelemetraText,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(unit, color = TelemetraTextDim, fontSize = 14.sp, modifier = Modifier.padding(bottom = 6.dp))
        }
    } else {
        Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                speed.toInt().toString(),
                color = TelemetraText,
                fontSize = if (large) 52.sp else 34.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(unit, color = TelemetraTextDim, fontSize = if (large) 15.sp else 12.sp)
        }
    }
}

@Composable
fun GearDisplay(
    gear: Int,
    modifier: Modifier = Modifier,
    fontSize: Int = 120,
    glow: Boolean = true,
) {
    val display = if (gear == 0) "N" else gear.toString()
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (glow) {
            Text(
                display,
                color = Color(0xFF22D3EE).copy(alpha = 0.25f),
                fontSize = (fontSize + 6).sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        }
        Text(
            display,
            color = TelemetraText,
            fontSize = fontSize.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ShiftLights(
    rpm: Float,
    rpmMax: Float,
    modifier: Modifier = Modifier,
) {
    val fraction = (rpm / rpmMax).coerceIn(0f, 1f)
    val lights = listOf(
        0.50f to Color(0xFF22C55E),
        0.58f to Color(0xFF22C55E),
        0.65f to Color(0xFF22C55E),
        0.72f to Color(0xFFEF4444),
        0.78f to Color(0xFFEF4444),
        0.84f to Color(0xFFEF4444),
        0.90f to Color(0xFFA855F7),
        0.96f to Color(0xFFA855F7),
    )
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        lights.forEach { (threshold, color) ->
            val lit = fraction >= threshold
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (lit) color else color.copy(alpha = 0.12f)),
            )
        }
    }
}

@Composable
fun RpmReadout(
    rpm: Float,
    rpmMax: Float,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                rpm.toInt().toString(),
                color = TelemetraText,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                " RPM",
                color = TelemetraText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 2.dp),
            )
        }
        Text("RPM Max ${rpmMax.toInt()}", color = TelemetraTextDim, fontSize = 11.sp)
    }
}
