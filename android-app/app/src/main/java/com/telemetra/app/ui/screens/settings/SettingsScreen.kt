package com.telemetra.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemetra.app.data.model.AppSettings
import com.telemetra.app.data.model.DisplayTheme
import com.telemetra.app.data.model.SpeedUnit
import com.telemetra.app.ui.theme.TelemetraBackground
import com.telemetra.app.ui.theme.TelemetraSurface
import com.telemetra.app.ui.theme.TelemetraTeal
import com.telemetra.app.ui.theme.TelemetraText
import com.telemetra.app.ui.theme.TelemetraTextDim

@Composable
fun SettingsScreen(
    settings: AppSettings,
    onThemeChange: (DisplayTheme) -> Unit,
    onSpeedUnitChange: (SpeedUnit) -> Unit,
    onSimulatorToggle: () -> Unit,
    onConnectionClick: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TelemetraBackground)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Retour", tint = TelemetraText)
            }
            Text("Réglages", color = TelemetraText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        SettingsSection(title = "Thème d'affichage") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ThemeChip(
                    label = "Gauge",
                    selected = settings.displayTheme == DisplayTheme.GAUGE,
                    onClick = { onThemeChange(DisplayTheme.GAUGE) },
                )
                ThemeChip(
                    label = "Cards",
                    selected = settings.displayTheme == DisplayTheme.CARDS,
                    onClick = { onThemeChange(DisplayTheme.CARDS) },
                )
            }
            Text(
                "Changement à chaud, sans redémarrage.",
                color = TelemetraTextDim,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp),
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSection(title = "Unité de vitesse") {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ThemeChip(
                    label = "km/h",
                    selected = settings.speedUnit == SpeedUnit.KMH,
                    onClick = { onSpeedUnitChange(SpeedUnit.KMH) },
                )
                ThemeChip(
                    label = "mph",
                    selected = settings.speedUnit == SpeedUnit.MPH,
                    onClick = { onSpeedUnitChange(SpeedUnit.MPH) },
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSection(title = "Mode simulateur") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Données mockées", color = TelemetraText, fontSize = 15.sp)
                    Text(
                        "Génère des télémétries aléatoires réalistes pour développer l'UI.",
                        color = TelemetraTextDim,
                        fontSize = 12.sp,
                    )
                }
                Switch(
                    checked = settings.simulatorMode,
                    onCheckedChange = { onSimulatorToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = TelemetraTeal,
                        checkedTrackColor = TelemetraTeal.copy(alpha = 0.3f),
                    ),
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        SettingsSection(title = "Connexion") {
            Text(
                "Relay : ${settings.relayHost}:${settings.relayPort}",
                color = TelemetraText,
                fontSize = 15.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ThemeChip(
                label = "Configurer la connexion →",
                selected = false,
                onClick = onConnectionClick,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Telemetra v0.1.0\nTélémétrie sim racing universelle",
            color = TelemetraTextDim,
            fontSize = 12.sp,
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(TelemetraSurface, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Text(title, color = TelemetraTeal, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
private fun ThemeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = TelemetraTeal,
            selectedLabelColor = TelemetraBackground,
            containerColor = TelemetraBackground,
            labelColor = TelemetraText,
        ),
    )
}
