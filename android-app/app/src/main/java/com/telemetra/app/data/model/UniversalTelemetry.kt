package com.telemetra.app.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class TirePosition {
    @SerialName("FL") FL,
    @SerialName("FR") FR,
    @SerialName("RL") RL,
    @SerialName("RR") RR,
}

@Serializable
data class TireTelemetry(
    val position: TirePosition,
    val tempC: Float,
    val wearPct: Float,
)

@Serializable
data class UniversalTelemetry(
    val game: String = "Forza",
    val sessionType: String = "Race",
    val track: String = "Le Mans",
    val timestamp: Long = System.currentTimeMillis(),
    val speedKmh: Float = 0f,
    val rpm: Float = 0f,
    val rpmMax: Float = 9500f,
    val gear: Int = 0,
    val throttlePct: Float = 0f,
    val brakePct: Float = 0f,
    val steeringAngle: Float = 0f,
    val fuelPct: Float = 100f,
    val lapNumber: Int = 0,
    val lapsLeft: Int = 0,
    val position: Int = 0,
    val lapDelta: Float = 0f,
    val bestLapTime: String = "--:--.---",
    val lastLapTime: String = "--:--.---",
    val tires: List<TireTelemetry> = defaultTires(),
) {
    companion object {
        fun defaultTires(): List<TireTelemetry> = listOf(
            TireTelemetry(TirePosition.FL, 25f, 0f),
            TireTelemetry(TirePosition.FR, 25f, 0f),
            TireTelemetry(TirePosition.RL, 25f, 0f),
            TireTelemetry(TirePosition.RR, 25f, 0f),
        )

        fun mock(): UniversalTelemetry = UniversalTelemetry(
            game = "Forza",
            sessionType = "Race",
            track = "Le Mans",
            speedKmh = 286f,
            rpm = 9008f,
            rpmMax = 9500f,
            gear = 8,
            throttlePct = 80f,
            brakePct = 10f,
            steeringAngle = 0f,
            fuelPct = 33f,
            lapNumber = 68,
            lapsLeft = 5,
            position = 3,
            lapDelta = -18.279f,
            bestLapTime = "01:26.564",
            lastLapTime = "01:23.260",
            tires = listOf(
                TireTelemetry(TirePosition.FL, 39f, 30f),
                TireTelemetry(TirePosition.FR, 36f, 90f),
                TireTelemetry(TirePosition.RL, 28f, 90f),
                TireTelemetry(TirePosition.RR, 28f, 90f),
            ),
        )
    }
}
