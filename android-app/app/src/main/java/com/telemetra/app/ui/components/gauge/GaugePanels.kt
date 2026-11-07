package com.telemetra.app.ui.components.gauge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemetra.app.R
import com.telemetra.app.data.model.TireTelemetry
import com.telemetra.app.ui.components.LapDeltaDisplay
import com.telemetra.app.ui.components.PedalInputBar
import com.telemetra.app.ui.theme.TelemetraBackground
import com.telemetra.app.ui.theme.TelemetraGreen
import com.telemetra.app.ui.theme.TelemetraRed
import com.telemetra.app.ui.theme.TelemetraText
import com.telemetra.app.ui.theme.TelemetraTextDim

private val TirePanelGradient = Brush.horizontalGradient(
    0f to Color(0xFF0A2E36),
    0.42f to Color(0xFF2E7D52),
    0.72f to Color(0xFF4CAF50),
    1f to Color(0xFF8B7D3A),
)

private val TireGroupBorder = Color(0xFF22D3EE)

private fun tireAccentColor(tempC: Float): Color {
    return when {
        tempC > 90f -> Color(0xFFE53935)
        tempC > 75f -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }
}

private fun tireRowBrush(accent: Color, mirrored: Boolean): Brush {
    val colors = listOf(
        accent.copy(alpha = 0.08f),
        accent.copy(alpha = 0.22f),
        accent.copy(alpha = 0.10f),
    )
    return if (mirrored) {
        Brush.horizontalGradient(colors.reversed())
    } else {
        Brush.horizontalGradient(colors)
    }
}

/** Bloc FL+RL ou FR+RR avec bordure cyan (style mockup). */
@Composable
fun GaugeTireGroup(
    topTire: TireTelemetry,
    bottomTire: TireTelemetry,
    modifier: Modifier = Modifier,
    mirrored: Boolean = false,
    showPicBadge: Boolean = false,
    useFahrenheit: Boolean = false,
) {
    Box(modifier = modifier.fillMaxWidth()) {
        if (showPicBadge) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .offset(x = 8.dp, y = (-7).dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(TireGroupBorder.copy(alpha = 0.25f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text("PIC", color = TireGroupBorder, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .drawBehind {
                    drawRoundRect(
                        color = TireGroupBorder.copy(alpha = 0.55f),
                        cornerRadius = CornerRadius(10.dp.toPx()),
                        style = Stroke(width = 1.4f),
                    )
                }
                .background(Color(0xFF0A1520).copy(alpha = 0.88f), RoundedCornerShape(10.dp)),
        ) {
            GaugeTireRow(
                tire = topTire,
                mirrored = mirrored,
                useFahrenheit = useFahrenheit,
                showBottomDivider = true,
            )
            GaugeTireRow(
                tire = bottomTire,
                mirrored = mirrored,
                useFahrenheit = useFahrenheit,
                showBottomDivider = false,
            )
        }
    }
}

@Composable
private fun GaugeTireRow(
    tire: TireTelemetry,
    mirrored: Boolean,
    useFahrenheit: Boolean,
    showBottomDivider: Boolean,
) {
    val accent = tireAccentColor(tire.tempC)
    val tempValue = if (useFahrenheit) {
        (tire.tempC * 9f / 5f + 32f).toInt()
    } else {
        tire.tempC.toInt()
    }
    val tempUnit = if (useFahrenheit) "°F" else "°C"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(tireRowBrush(accent, mirrored)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (!mirrored) {
                TireRowIcon(accent = accent)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    TireWearColumn(
                        positionLabel = tire.position.name,
                        wearPct = tire.wearPct,
                    )
                }
                TireTempColumn(
                    tempValue = tempValue,
                    tempUnit = tempUnit,
                    tempColor = accent,
                    modifier = Modifier.padding(end = 4.dp),
                )
            } else {
                TireTempColumn(
                    tempValue = tempValue,
                    tempUnit = tempUnit,
                    tempColor = accent,
                    modifier = Modifier.padding(start = 4.dp),
                )
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    TireWearColumn(
                        positionLabel = tire.position.name,
                        wearPct = tire.wearPct,
                    )
                }
                TireRowIcon(accent = accent)
            }
        }

        if (showBottomDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(TireGroupBorder.copy(alpha = 0.25f)),
            )
        }
    }
}

@Composable
private fun TireRowIcon(accent: Color) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(accent.copy(alpha = 0.12f)),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_tire),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
private fun TireWearColumn(
    positionLabel: String,
    wearPct: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(positionLabel, color = TelemetraTextDim, fontSize = 8.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "${wearPct.toInt()}%",
            color = TelemetraText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
        )
        Text("usure", color = TelemetraTextDim, fontSize = 9.sp)
    }
}

@Composable
private fun TireTempColumn(
    tempValue: Int,
    tempUnit: String,
    tempColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "$tempValue$tempUnit",
            color = tempColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
fun GaugeStatsGrid(
    fuelPct: Float,
    laps: Int,
    position: Int,
    lapsLeft: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(TelemetraBackground.copy(alpha = 0.6f)),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val glow = Color(0xFF22D3EE)
            val midX = size.width / 2f
            val midY = size.height / 2f

            drawLine(
                brush = Brush.horizontalGradient(
                    0f to Color.Transparent,
                    0.35f to glow.copy(alpha = 0.15f),
                    0.5f to glow.copy(alpha = 0.85f),
                    0.65f to glow.copy(alpha = 0.15f),
                    1f to Color.Transparent,
                ),
                start = Offset(0f, midY),
                end = Offset(size.width, midY),
                strokeWidth = 1.5f,
            )
            drawLine(
                brush = Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.35f to glow.copy(alpha = 0.15f),
                    0.5f to glow.copy(alpha = 0.55f),
                    0.65f to glow.copy(alpha = 0.15f),
                    1f to Color.Transparent,
                ),
                start = Offset(midX, 0f),
                end = Offset(midX, size.height),
                strokeWidth = 1f,
            )
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.weight(1f)) {
                GridCell("Fuel Left:", "${fuelPct.toInt()}%", Modifier.weight(1f))
                GridCell("Laps:", laps.toString(), Modifier.weight(1f))
            }
            Row(modifier = Modifier.weight(1f)) {
                GridCell("Pos:", position.toString(), Modifier.weight(1f))
                GridCell("Laps Left:", lapsLeft.toString(), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun GridCell(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(label, color = TelemetraText, fontSize = 11.sp, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, color = TelemetraText, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun GaugeRightPanel(
    lapDelta: Float,
    bestLapTime: String,
    lastLapTime: String,
    throttlePct: Float,
    brakePct: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .wrapContentWidth(Alignment.Start)
                .padding(start = 0.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Text("Lap Delta:", color = TelemetraTextDim, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(2.dp))
                LapDeltaDisplay(delta = lapDelta, fontSize = 20)
            }
            GaugeLapStat(label = "Best Lap:", value = bestLapTime)
            GaugeLapStat(label = "Last Lap:", value = lastLapTime)
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            PedalInputBar(
                value = throttlePct,
                label = "Throttle",
                color = TelemetraGreen,
            )
            PedalInputBar(
                value = brakePct,
                label = "Brake",
                color = TelemetraRed,
            )
        }
    }
}

@Composable
private fun GaugeLapStat(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(vertical = 4.dp),
    ) {
        Text(label, color = TelemetraTextDim, fontSize = 11.sp)
        Text(
            value,
            color = TelemetraText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun GaugeTirePanel(
    tire: TireTelemetry,
    modifier: Modifier = Modifier,
    useFahrenheit: Boolean = false,
) {
    val tempValue = if (useFahrenheit) {
        (tire.tempC * 9f / 5f + 32f).toInt()
    } else {
        tire.tempC.toInt()
    }
    val tempUnit = if (useFahrenheit) "°F" else "°C"

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .drawBehind {
                drawRoundRect(
                    brush = TirePanelGradient,
                    cornerRadius = CornerRadius(12.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            .background(TirePanelGradient, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                tire.position.name,
                color = TelemetraText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp),
            )

            Image(
                painter = painterResource(R.drawable.ic_tire),
                contentDescription = null,
                modifier = Modifier.size(34.dp),
            )

            Column(modifier = Modifier.padding(start = 6.dp)) {
                Text(
                    "${tire.wearPct.toInt()}%",
                    color = TelemetraText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text("usure", color = TelemetraTextDim, fontSize = 9.sp)
            }

            Spacer(modifier = Modifier.weight(1f))

            VerticalDivider(
                modifier = Modifier.height(30.dp),
                color = Color.White.copy(alpha = 0.25f),
            )

            Text(
                text = "$tempValue$tempUnit",
                color = TelemetraText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Clip,
            )
        }
    }
}
