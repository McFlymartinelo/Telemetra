package com.telemetra.app.ui.screens.cards

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemetra.app.data.model.ConnectionState
import com.telemetra.app.data.model.ConnectionStatus
import com.telemetra.app.data.model.UniversalTelemetry
import com.telemetra.app.ui.components.GearDisplay
import com.telemetra.app.ui.components.LapDeltaDisplay
import com.telemetra.app.ui.components.RpmArcGauge
import com.telemetra.app.ui.components.SpeedDisplay
import com.telemetra.app.ui.components.SteeringArcGauge
import com.telemetra.app.ui.components.TealCard
import com.telemetra.app.ui.components.TireCardSimple
import com.telemetra.app.ui.components.VerticalInputBar
import com.telemetra.app.ui.theme.TelemetraBackground
import com.telemetra.app.ui.theme.TelemetraGreen
import com.telemetra.app.ui.theme.TelemetraRed
import com.telemetra.app.ui.theme.TelemetraSurface
import com.telemetra.app.ui.theme.TelemetraTeal
import com.telemetra.app.ui.theme.TelemetraText
import com.telemetra.app.ui.theme.TelemetraTextDim
import com.telemetra.app.ui.theme.deltaColor

@Composable
fun CardsThemeScreen(
    telemetry: UniversalTelemetry,
    connectionStatus: ConnectionStatus,
    useMph: Boolean,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TelemetraBackground)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        RpmArcGauge(
            rpm = telemetry.rpm,
            rpmMax = telemetry.rpmMax,
            modifier = Modifier.fillMaxWidth(0.85f),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .width(64.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    InputBarColumn("Throttle", telemetry.throttlePct, TelemetraGreen)
                    InputBarColumn("Brake", telemetry.brakePct, TelemetraRed)
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier
                    .width(118.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            ) {
                TealCard("FUEL LEFT", "${telemetry.fuelPct.toInt()}%")
                TealCard("POSITION", telemetry.position.toString())
                TealCard("LAPS", telemetry.lapNumber.toString())
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.5.dp, TelemetraTeal.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                TelemetraSurface.copy(alpha = 0.7f),
                                TelemetraSurface.copy(alpha = 0.35f),
                            ),
                        ),
                    )
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text("GEAR", color = TelemetraTextDim, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                GearDisplay(gear = telemetry.gear, fontSize = 88, glow = true)
                SpeedDisplay(
                    speedKmh = telemetry.speedKmh,
                    useMph = useMph,
                    inline = true,
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier
                    .width(138.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.5.dp, TelemetraTeal.copy(alpha = 0.55f), RoundedCornerShape(14.dp))
                        .background(TelemetraSurface.copy(alpha = 0.45f))
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("LAP DELTA", color = TelemetraTextDim, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(4.dp))
                    LapDeltaDisplay(delta = telemetry.lapDelta, fontSize = 20)
                }
                TealCard("BEST LAP", telemetry.bestLapTime, valueColor = TelemetraText)
                TealCard("LAST LAP", telemetry.lastLapTime)
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
                modifier = Modifier
                    .width(90.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                SteeringArcGauge(
                    steeringAngle = telemetry.steeringAngle,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                )
                Text("Steering angle", color = TelemetraTextDim, fontSize = 9.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            telemetry.tires.forEach { tire ->
                TireCardSimple(tire = tire, modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        CardsBottomBar(
            telemetry = telemetry,
            connectionStatus = connectionStatus,
            onSettingsClick = onSettingsClick,
        )
    }
}

@Composable
private fun InputBarColumn(
    label: String,
    value: Float,
    color: androidx.compose.ui.graphics.Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TelemetraTextDim, fontSize = 8.sp)
        Spacer(modifier = Modifier.height(2.dp))
        VerticalInputBar(
            value = value,
            label = label,
            color = color,
            modifier = Modifier
                .width(24.dp)
                .height(100.dp),
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            "${value.toInt()}%",
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CardsBottomBar(
    telemetry: UniversalTelemetry,
    connectionStatus: ConnectionStatus,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(TelemetraSurface.copy(alpha = 0.35f))
            .padding(horizontal = 12.dp, vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Session: ${telemetry.sessionType}", color = TelemetraTextDim, fontSize = 10.sp)
            Text("Track: ${telemetry.track}", color = TelemetraTextDim, fontSize = 10.sp)
        }
        Text("Game: ${telemetry.game}", color = TelemetraTeal, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (connectionStatus.state == ConnectionState.CONNECTED) "Wi-Fi: Connected" else connectionStatus.message,
                color = if (connectionStatus.state == ConnectionState.CONNECTED) TelemetraGreen else TelemetraTextDim,
                fontSize = 10.sp,
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("◉ Universal Telemetry", color = TelemetraTextDim, fontSize = 10.sp)
            IconButton(onClick = onSettingsClick, modifier = Modifier.height(32.dp)) {
                Icon(Icons.Default.Settings, "Réglages", tint = TelemetraTextDim)
            }
        }
    }
}
