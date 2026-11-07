package com.telemetra.app.ui.screens.connection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.telemetra.app.data.model.ConnectionState
import com.telemetra.app.data.model.ConnectionStatus
import com.telemetra.app.ui.theme.TelemetraBackground
import com.telemetra.app.ui.theme.TelemetraGreen
import com.telemetra.app.ui.theme.TelemetraRed
import com.telemetra.app.ui.theme.TelemetraSurface
import com.telemetra.app.ui.theme.TelemetraTeal
import com.telemetra.app.ui.theme.TelemetraText
import com.telemetra.app.ui.theme.TelemetraTextDim

@Composable
fun ConnectionScreen(
    connectionStatus: ConnectionStatus,
    relayHost: String,
    relayPort: Int,
    simulatorMode: Boolean,
    onConnect: (host: String, port: Int) -> Unit,
    onDisconnect: () -> Unit,
    onEnableSimulator: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var host by rememberSaveable { mutableStateOf(relayHost) }
    var port by rememberSaveable { mutableStateOf(relayPort.toString()) }

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
            Text("Connexion", color = TelemetraText, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Status card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(TelemetraSurface, RoundedCornerShape(12.dp))
                .padding(20.dp),
        ) {
            Text("Statut", color = TelemetraTextDim, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val statusColor = when (connectionStatus.state) {
                ConnectionState.CONNECTED -> TelemetraGreen
                ConnectionState.ERROR -> TelemetraRed
                else -> TelemetraTextDim
            }
            Text(connectionStatus.message, color = statusColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            if (connectionStatus.activeGame.isNotEmpty()) {
                Text("Jeu : ${connectionStatus.activeGame}", color = TelemetraTextDim, fontSize = 14.sp)
            }
            if (simulatorMode) {
                Text("Mode simulateur actif", color = TelemetraTeal, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = host,
            onValueChange = { host = it },
            label = { Text("IP du PC relais") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            colors = fieldColors(),
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = port,
            onValueChange = { port = it.filter { c -> c.isDigit() } },
            label = { Text("Port WebSocket") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = fieldColors(),
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Button(
                onClick = {
                    val portInt = port.toIntOrNull() ?: 8888
                    onConnect(host, portInt)
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TelemetraTeal),
            ) {
                Text("Connecter", color = TelemetraBackground, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onDisconnect,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = TelemetraSurface),
            ) {
                Text("Déconnecter", color = TelemetraText)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onEnableSimulator,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = TelemetraSurface),
        ) {
            Text("Activer le mode simulateur", color = TelemetraTeal)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Le relay-server doit tourner sur le PC de jeu.\n" +
                "Par défaut : ws://<IP_PC>:8888\n\n" +
                "En développement, utilisez le mode simulateur pour tester l'UI sans serveur.",
            color = TelemetraTextDim,
            fontSize = 13.sp,
            lineHeight = 20.sp,
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TelemetraText,
    unfocusedTextColor = TelemetraText,
    focusedBorderColor = TelemetraTeal,
    unfocusedBorderColor = TelemetraTextDim,
    focusedLabelColor = TelemetraTeal,
    unfocusedLabelColor = TelemetraTextDim,
    cursorColor = TelemetraTeal,
)
