package com.telemetra.app.data.model

enum class DisplayTheme {
    GAUGE,
    CARDS,
}

enum class SpeedUnit {
    KMH,
    MPH,
}

enum class ConnectionState {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    ERROR,
}

data class AppSettings(
    val displayTheme: DisplayTheme = DisplayTheme.GAUGE,
    val speedUnit: SpeedUnit = SpeedUnit.KMH,
    val simulatorMode: Boolean = true,
    val relayHost: String = "192.168.1.42",
    val relayPort: Int = 8888,
)

data class ConnectionStatus(
    val state: ConnectionState = ConnectionState.DISCONNECTED,
    val message: String = "Déconnecté",
    val activeGame: String = "",
)
