package com.telemetra.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Jauge RPM segmentée et aplatie (arc peu profond), dégradé vert → jaune → rouge,
 * indicateur blanc lumineux sur le segment actif.
 */
@Composable
fun RpmGauge(
    rpm: Float,
    maxRpm: Float,
    modifier: Modifier = Modifier,
    segmentCount: Int = 22,
    startAngleDeg: Float = 222f,
    sweepAngleDeg: Float = 96f,
    trackColorLow: Color = Color(0xFF4CD964),
    trackColorMid: Color = Color(0xFFFFC940),
    trackColorHigh: Color = Color(0xFFFF3B30),
    indicatorColor: Color = Color.White,
) {
    val progress = (rpm / maxRpm).coerceIn(0f, 1f)
    val activeSegmentIndex = (progress * (segmentCount - 1)).toInt().coerceIn(0, segmentCount - 1)

    // aspectRatio ~5.8 : arc encore plus plat (sweep 96°).
    Box(modifier = modifier.fillMaxWidth().aspectRatio(5.8f)) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val strokeWidth = size.width * 0.034f

            // --- Géométrie "aplatie" ---
            // Le rayon est calculé à partir de la LARGEUR (pas de la hauteur) pour obtenir un
            // grand cercle dont on ne montre que la fine calotte du haut. Le centre du cercle est
            // placé bien en dessous du canvas visible : c'est ça qui donne l'effet "aplati mais large".
            val halfSweepRad = Math.toRadians((sweepAngleDeg / 2f).toDouble())
            val radius = (size.width / 2f) / kotlin.math.sin(halfSweepRad).toFloat() - strokeWidth / 2f
            val topPadding = strokeWidth / 2f
            val center = Offset(size.width / 2f, topPadding + radius)

            val gapDeg = 0.9f // espace fin entre segments -> blocs pleins et collés, pas des tirets
            val stepDeg = sweepAngleDeg / segmentCount
            val segmentSweep = stepDeg - gapDeg

            for (i in 0 until segmentCount) {
                val segStart = startAngleDeg + i * stepDeg + gapDeg / 2f
                val t = i / (segmentCount - 1f).coerceAtLeast(1f)
                val isActive = i == activeSegmentIndex
                val segColor = if (isActive) {
                    indicatorColor
                } else {
                    rpmColorForPosition(t, trackColorLow, trackColorMid, trackColorHigh)
                }

                drawArc(
                    color = segColor,
                    startAngle = segStart,
                    sweepAngle = segmentSweep,
                    useCenter = false,
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2f, radius * 2f),
                    style = Stroke(
                        width = if (isActive) strokeWidth * 1.35f else strokeWidth,
                        cap = StrokeCap.Butt,
                    ),
                )
            }

            // Halo doux et discret derrière le segment actif (pas de cap arrondi surdimensionné
            // pour éviter l'effet "pastille" isolée).
            val activeStart = startAngleDeg + activeSegmentIndex * stepDeg + gapDeg / 2f
            drawArc(
                color = indicatorColor.copy(alpha = 0.25f),
                startAngle = activeStart - 1f,
                sweepAngle = segmentSweep + 2f,
                useCenter = false,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = Size(radius * 2f, radius * 2f),
                style = Stroke(width = strokeWidth * 2.2f, cap = StrokeCap.Butt),
            )
        }
    }
}

/** Wrapper conservé pour les écrans existants. */
@Composable
fun RpmArcGauge(
    rpm: Float,
    rpmMax: Float,
    modifier: Modifier = Modifier,
) {
    RpmGauge(rpm = rpm, maxRpm = rpmMax, modifier = modifier)
}

private fun rpmColorForPosition(t: Float, low: Color, mid: Color, high: Color): Color {
    return if (t < 0.6f) {
        lerp(low, mid, (t / 0.6f).coerceIn(0f, 1f))
    } else {
        lerp(mid, high, ((t - 0.6f) / 0.4f).coerceIn(0f, 1f))
    }
}

@Composable
fun SteeringArcGauge(
    steeringAngle: Float,
    modifier: Modifier = Modifier,
) {
    val maxAngle = 90f
    val fraction = ((steeringAngle + maxAngle) / (2f * maxAngle)).coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val trackColor = Color(0xFF0E3A4A).copy(alpha = 0.55f)
        val fillColor = Color(0xFF22D3EE)
        val fillDim = Color(0xFF0E4F6A).copy(alpha = 0.5f)

        val horizontalPadding = size.width * 0.04f
        val barHeight = (size.height * 0.42f).coerceIn(8f, 16f)
        val thinHeight = 2f
        val barTop = size.height * 0.55f - barHeight / 2f
        val thinTop = barTop - thinHeight - 4f
        val trackLeft = horizontalPadding
        val trackWidth = size.width - horizontalPadding * 2f
        val fillWidth = trackWidth * fraction
        val corner = CornerRadius(barHeight / 2f, barHeight / 2f)

        // Piste inactive
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(trackLeft, barTop),
            size = Size(trackWidth, barHeight),
            cornerRadius = corner,
        )

        // Remplissage teal de gauche jusqu'à la position actuelle
        if (fillWidth > 0f) {
            drawRoundRect(
                color = fillColor,
                topLeft = Offset(trackLeft, barTop),
                size = Size(fillWidth, barHeight),
                cornerRadius = corner,
            )
        }

        // Partie inactive à droite (plus sombre)
        if (fillWidth < trackWidth) {
            drawRoundRect(
                color = fillDim,
                topLeft = Offset(trackLeft + fillWidth, barTop),
                size = Size(trackWidth - fillWidth, barHeight),
                cornerRadius = corner,
            )
        }

        // Ligne fine au-dessus
        drawRoundRect(
            color = trackColor.copy(alpha = 0.4f),
            topLeft = Offset(trackLeft, thinTop),
            size = Size(trackWidth, thinHeight),
            cornerRadius = CornerRadius(1f, 1f),
        )
        if (fillWidth > 0f) {
            drawRoundRect(
                color = fillColor.copy(alpha = 0.9f),
                topLeft = Offset(trackLeft, thinTop),
                size = Size(fillWidth, thinHeight),
                cornerRadius = CornerRadius(1f, 1f),
            )
        }

        // Marqueur central (zéro)
        val centerX = trackLeft + trackWidth / 2f
        drawLine(
            color = Color.White.copy(alpha = 0.85f),
            start = Offset(centerX, barTop - 3f),
            end = Offset(centerX, barTop + barHeight + 3f),
            strokeWidth = 2f,
            cap = StrokeCap.Round,
        )

        // Indicateur de position actuelle
        val posX = trackLeft + trackWidth * fraction
        drawLine(
            color = Color.White,
            start = Offset(posX, barTop - 1f),
            end = Offset(posX, barTop + barHeight + 1f),
            strokeWidth = 2.5f,
            cap = StrokeCap.Round,
        )
    }
}

/** Barre d'entrée verticale (throttle / brake) avec % dans la barre et label en dessous. */
@Composable
fun PedalInputBar(
    value: Float,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    barHeight: androidx.compose.ui.unit.Dp = 108.dp,
    barWidth: androidx.compose.ui.unit.Dp = 30.dp,
) {
    val fraction = (value / 100f).coerceIn(0f, 1f)
    val pctText = "${value.toInt()}%"

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .width(barWidth)
                .height(barHeight),
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val corner = CornerRadius(8f, 8f)
                val inset = 1.5f

                drawRoundRect(
                    color = Color.White.copy(alpha = 0.1f),
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2f, size.height - inset * 2f),
                    cornerRadius = corner,
                )
                drawRoundRect(
                    color = Color.White.copy(alpha = 0.22f),
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - inset * 2f, size.height - inset * 2f),
                    cornerRadius = corner,
                    style = Stroke(width = 1.2f),
                )

                val fillHeight = (size.height - inset * 2f) * fraction
                if (fillHeight > 0f) {
                    drawRoundRect(
                        color = color,
                        topLeft = Offset(inset, size.height - inset - fillHeight),
                        size = Size(size.width - inset * 2f, fillHeight),
                        cornerRadius = corner,
                    )
                }
            }

            Text(
                text = pctText,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = maxHeight * fraction + 3.dp),
            )
        }

        Text(
            text = label,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 4.dp),
        )
    }
}

@Composable
fun VerticalInputBar(
    value: Float,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    val fraction = (value / 100f).coerceIn(0f, 1f)

    Canvas(modifier = modifier) {
        val barWidth = size.width * 0.55f
        val left = (size.width - barWidth) / 2f
        val height = size.height

        drawRoundRect(
            color = Color.White.copy(alpha = 0.08f),
            topLeft = Offset(left, 0f),
            size = Size(barWidth, height),
            cornerRadius = CornerRadius(6f, 6f),
        )

        val fillHeight = height * fraction
        if (fillHeight > 0f) {
            drawRoundRect(
                color = color,
                topLeft = Offset(left, height - fillHeight),
                size = Size(barWidth, fillHeight),
                cornerRadius = CornerRadius(6f, 6f),
            )
        }
    }
}
