package com.telemetra.app.data.repository

import com.telemetra.app.data.model.ConnectionState
import com.telemetra.app.data.model.ConnectionStatus
import com.telemetra.app.data.model.TirePosition
import com.telemetra.app.data.model.TireTelemetry
import com.telemetra.app.data.model.UniversalTelemetry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import kotlin.math.sin
import kotlin.random.Random

class TelemetryRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var simulatorJob: Job? = null
    private var webSocket: WebSocket? = null

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val _telemetry = MutableStateFlow(UniversalTelemetry.mock())
    val telemetry: StateFlow<UniversalTelemetry> = _telemetry.asStateFlow()

    private val _connectionStatus = MutableStateFlow(
        ConnectionStatus(state = ConnectionState.DISCONNECTED, message = "Mode simulateur")
    )
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()

    private var tick = 0

    fun startSimulator() {
        stopAll()
        _connectionStatus.value = ConnectionStatus(
            state = ConnectionState.CONNECTED,
            message = "Simulateur actif",
            activeGame = "Forza (mock)",
        )
        simulatorJob = scope.launch {
            var base = UniversalTelemetry.mock()
            while (isActive) {
                tick++
                val phase = tick * 0.08f
                val rpmWave = sin(phase.toDouble()).toFloat() * 0.5f + 0.5f
                val rpm = (3000f + rpmWave * 6500f).coerceIn(0f, base.rpmMax)
                val speed = (80f + rpmWave * 220f + Random.nextFloat() * 10f).coerceIn(0f, 320f)
                val gear = when {
                    speed < 60 -> 2
                    speed < 120 -> 3
                    speed < 170 -> 4
                    speed < 210 -> 5
                    speed < 250 -> 6
                    speed < 280 -> 7
                    else -> 8
                }
                val steering = sin((phase * 1.3f).toDouble()).toFloat() * 45f
                val throttle = (sin((phase * 0.7f).toDouble()).toFloat() * 0.5f + 0.5f) * 100f
                val brake = if (sin((phase * 2.1f).toDouble()) > 0.85) 60f else (10f + Random.nextFloat() * 5f)

                base = base.copy(
                    timestamp = System.currentTimeMillis(),
                    speedKmh = speed,
                    rpm = rpm,
                    gear = gear,
                    throttlePct = throttle.coerceIn(0f, 100f),
                    brakePct = brake,
                    steeringAngle = steering,
                    fuelPct = (base.fuelPct - 0.02f).coerceAtLeast(5f),
                    lapDelta = -18.279f + sin((phase * 0.5f).toDouble()).toFloat() * 2f,
                    tires = listOf(
                        TireTelemetry(TirePosition.FL, 35f + rpmWave * 60f, 30f + Random.nextFloat() * 5f),
                        TireTelemetry(TirePosition.FR, 33f + rpmWave * 61f, 85f + Random.nextFloat() * 5f),
                        TireTelemetry(TirePosition.RL, 26f + rpmWave * 65f, 88f + Random.nextFloat() * 5f),
                        TireTelemetry(TirePosition.RR, 27f + rpmWave * 59f, 90f + Random.nextFloat() * 5f),
                    ),
                )
                _telemetry.value = base
                delay(50) // ~20 Hz
            }
        }
    }

    fun connect(host: String, port: Int) {
        stopAll()
        val wsUrl = buildWsUrl(host, port)
        _connectionStatus.value = ConnectionStatus(
            state = ConnectionState.CONNECTING,
            message = "Connexion à $wsUrl…",
        )

        val request = Request.Builder().url(wsUrl).build()
        webSocket = httpClient.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                scope.launch {
                    _connectionStatus.value = ConnectionStatus(
                        state = ConnectionState.CONNECTED,
                        message = "Connecté - Wi-Fi",
                        activeGame = _telemetry.value.game.ifBlank { "relay" },
                    )
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                runCatching {
                    json.decodeFromString<UniversalTelemetry>(text)
                }.onSuccess { payload ->
                    scope.launch {
                        _telemetry.value = payload
                        if (_connectionStatus.value.state == ConnectionState.CONNECTED) {
                            _connectionStatus.value = _connectionStatus.value.copy(
                                activeGame = payload.game,
                            )
                        }
                    }
                }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                webSocket.close(code, reason)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                scope.launch {
                    if (_connectionStatus.value.state != ConnectionState.DISCONNECTED) {
                        _connectionStatus.value = ConnectionStatus(
                            state = ConnectionState.DISCONNECTED,
                            message = reason.ifBlank { "Connexion fermée" },
                        )
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                scope.launch {
                    _connectionStatus.value = ConnectionStatus(
                        state = ConnectionState.ERROR,
                        message = connectionErrorMessage(t, response),
                    )
                }
            }
        })
    }

    fun disconnect() {
        stopAll()
        _connectionStatus.value = ConnectionStatus(
            state = ConnectionState.DISCONNECTED,
            message = "Déconnecté",
        )
        _telemetry.value = UniversalTelemetry()
    }

    private fun stopAll() {
        simulatorJob?.cancel()
        simulatorJob = null
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
    }

    private fun buildWsUrl(host: String, port: Int): String {
        val trimmed = host.trim()
        if (trimmed.startsWith("ws://") || trimmed.startsWith("wss://")) {
            return trimmed.removeSuffix("/")
        }
        val hostOnly = trimmed
            .substringBefore("/")
            .substringBefore(":")
            .trim()
        return "ws://$hostOnly:$port"
    }

    private fun connectionErrorMessage(t: Throwable, response: Response?): String {
        val detail = t.message?.takeIf { it.isNotBlank() }
        val httpCode = response?.code?.takeIf { it > 0 }
        return when {
            detail != null && httpCode != null -> "Échec connexion ($httpCode) : $detail"
            detail != null -> "Échec connexion : $detail"
            httpCode != null -> "Échec connexion (HTTP $httpCode)"
            else -> "Échec connexion — vérifiez l'IP, le port et que le relay tourne sur le PC"
        }
    }
}
