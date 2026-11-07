package com.telemetra.app.ui.screens.gauge

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemetra.app.data.model.ConnectionState
import com.telemetra.app.data.model.ConnectionStatus
import com.telemetra.app.data.model.TirePosition
import com.telemetra.app.data.model.UniversalTelemetry
import com.telemetra.app.ui.components.GearDisplay
import com.telemetra.app.ui.components.RpmArcGauge
import com.telemetra.app.ui.components.RpmReadout
import com.telemetra.app.ui.components.ShiftLights
import com.telemetra.app.ui.components.SpeedDisplay
import com.telemetra.app.ui.components.SteeringArcGauge
import com.telemetra.app.ui.components.gauge.GaugeRightPanel
import com.telemetra.app.ui.components.gauge.GaugeStatsGrid
import com.telemetra.app.ui.components.gauge.GaugeTireGroup
import com.telemetra.app.ui.theme.TelemetraBackground
import com.telemetra.app.ui.theme.TelemetraGreen
import com.telemetra.app.ui.theme.TelemetraText
import com.telemetra.app.ui.theme.TelemetraTextDim

private val GaugeSideColumnWidth = 188.dp

@Composable
fun GaugeThemeScreen(
    telemetry: UniversalTelemetry,
    connectionStatus: ConnectionStatus,
    useMph: Boolean,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val tire = { pos: TirePosition ->
        telemetry.tires.firstOrNull { it.position == pos } ?: telemetry.tires.first()
    }
    val useFahrenheit = false // km/h mode uses °C; could tie to settings later

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TelemetraBackground)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    ) {
        GaugeTopBar(connectionStatus = connectionStatus, onSettingsClick = onSettingsClick)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            // ── Colonne gauche ──
            Column(
                modifier = Modifier
                    .width(GaugeSideColumnWidth)
                    .fillMaxHeight(),
            ) {
                GaugeStatsGrid(
                    fuelPct = telemetry.fuelPct,
                    laps = telemetry.lapNumber,
                    position = telemetry.position,
                    lapsLeft = telemetry.lapsLeft,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                )
                Spacer(modifier = Modifier.height(6.dp))
                GaugeTireGroup(
                    topTire = tire(TirePosition.FL),
                    bottomTire = tire(TirePosition.RL),
                    showPicBadge = true,
                    useFahrenheit = useFahrenheit,
                )
            }

            // ── Colonne centrale : shift lights, arc RPM, rapport + vitesse au milieu ──
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            ) {
                val gearSize = (maxHeight.value * 0.24f).toInt().coerceIn(44, 72)
                val arcContentTop = maxWidth * 0.72f / 5.8f * 0.36f

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        ShiftLights(rpm = telemetry.rpm, rpmMax = telemetry.rpmMax)
                        Spacer(modifier = Modifier.height(14.dp))

                        // Arc RPM + lecture / rapport / vitesse superposés dans la calotte
                        BoxWithConstraints(
                            modifier = Modifier.fillMaxWidth(0.72f),
                        ) {
                            RpmArcGauge(
                                rpm = telemetry.rpm,
                                rpmMax = telemetry.rpmMax,
                                modifier = Modifier.fillMaxWidth(),
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = arcContentTop),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                RpmReadout(rpm = telemetry.rpm, rpmMax = telemetry.rpmMax)
                                GearDisplay(
                                    gear = telemetry.gear,
                                    fontSize = gearSize,
                                    glow = true,
                                    modifier = Modifier.offset(y = (-4).dp),
                                )
                                SpeedDisplay(
                                    speedKmh = telemetry.speedKmh,
                                    useMph = useMph,
                                    large = false,
                                    modifier = Modifier.offset(y = (-6).dp),
                                )
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        SteeringArcGauge(
                            steeringAngle = telemetry.steeringAngle,
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .height(32.dp),
                        )
                        Text("Steering", color = TelemetraTextDim, fontSize = 10.sp)
                    }
                }
            }

            // ── Colonne droite ──
            Column(
                modifier = Modifier
                    .width(GaugeSideColumnWidth)
                    .fillMaxHeight(),
            ) {
                GaugeRightPanel(
                    lapDelta = telemetry.lapDelta,
                    bestLapTime = telemetry.bestLapTime,
                    lastLapTime = telemetry.lastLapTime,
                    throttlePct = telemetry.throttlePct,
                    brakePct = telemetry.brakePct,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(6.dp))
                GaugeTireGroup(
                    topTire = tire(TirePosition.FR),
                    bottomTire = tire(TirePosition.RR),
                    mirrored = true,
                    useFahrenheit = useFahrenheit,
                )
            }
        }
    }
}

@Composable
private fun GaugeTopBar(
    connectionStatus: ConnectionStatus,
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(26.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("◉", color = TelemetraText, fontSize = 15.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                "Universal Telemetry",
                color = TelemetraText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Wifi,
                "Wi-Fi",
                tint = if (connectionStatus.state == ConnectionState.CONNECTED) TelemetraGreen else TelemetraTextDim,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                if (connectionStatus.state == ConnectionState.CONNECTED) "Connected - Wi-Fi" else connectionStatus.message,
                color = if (connectionStatus.state == ConnectionState.CONNECTED) TelemetraGreen else TelemetraTextDim,
                fontSize = 10.sp,
            )
            IconButton(onClick = onSettingsClick, modifier = Modifier.size(30.dp)) {
                Icon(Icons.Default.Settings, "Réglages", tint = TelemetraTextDim)
            }
        }
    }
}
