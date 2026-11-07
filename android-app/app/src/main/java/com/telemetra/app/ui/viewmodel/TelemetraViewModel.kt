package com.telemetra.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.telemetra.app.data.model.AppSettings
import com.telemetra.app.data.model.ConnectionStatus
import com.telemetra.app.data.model.DisplayTheme
import com.telemetra.app.data.model.SpeedUnit
import com.telemetra.app.data.model.UniversalTelemetry
import com.telemetra.app.data.preferences.SettingsRepository
import com.telemetra.app.data.repository.TelemetryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TelemetraViewModel(application: Application) : AndroidViewModel(application) {

    private val settingsRepository = SettingsRepository(application)
    private val telemetryRepository = TelemetryRepository()

    val telemetry: StateFlow<UniversalTelemetry> = telemetryRepository.telemetry
    val connectionStatus: StateFlow<ConnectionStatus> = telemetryRepository.connectionStatus

    val settings: StateFlow<AppSettings> = settingsRepository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    private val _initialized = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { appSettings ->
                if (!_initialized.value) {
                    _initialized.value = true
                    if (appSettings.simulatorMode) {
                        telemetryRepository.startSimulator()
                    }
                }
            }
        }
    }

    fun connect(host: String, port: Int) {
        viewModelScope.launch {
            settingsRepository.updateSettings {
                it.copy(relayHost = host, relayPort = port, simulatorMode = false)
            }
            telemetryRepository.connect(host, port)
        }
    }

    fun disconnect() {
        viewModelScope.launch {
            telemetryRepository.disconnect()
        }
    }

    fun setSimulatorMode(enabled: Boolean) {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(simulatorMode = enabled) }
            if (enabled) {
                telemetryRepository.startSimulator()
            } else {
                telemetryRepository.disconnect()
            }
        }
    }

    fun setDisplayTheme(theme: DisplayTheme) {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(displayTheme = theme) }
        }
    }

    fun setSpeedUnit(unit: SpeedUnit) {
        viewModelScope.launch {
            settingsRepository.updateSettings { it.copy(speedUnit = unit) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        telemetryRepository.disconnect()
    }
}
