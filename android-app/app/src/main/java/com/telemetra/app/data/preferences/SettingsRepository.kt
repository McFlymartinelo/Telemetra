package com.telemetra.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.telemetra.app.data.model.AppSettings
import com.telemetra.app.data.model.DisplayTheme
import com.telemetra.app.data.model.SpeedUnit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "telemetra_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val DISPLAY_THEME = stringPreferencesKey("display_theme")
        val SPEED_UNIT = stringPreferencesKey("speed_unit")
        val SIMULATOR_MODE = booleanPreferencesKey("simulator_mode")
        val RELAY_HOST = stringPreferencesKey("relay_host")
        val RELAY_PORT = intPreferencesKey("relay_port")
    }

    val settings: Flow<AppSettings> = context.dataStore.data.map { prefs ->
        AppSettings(
            displayTheme = prefs[Keys.DISPLAY_THEME]?.let {
                runCatching { DisplayTheme.valueOf(it) }.getOrDefault(DisplayTheme.GAUGE)
            } ?: DisplayTheme.GAUGE,
            speedUnit = prefs[Keys.SPEED_UNIT]?.let {
                runCatching { SpeedUnit.valueOf(it) }.getOrDefault(SpeedUnit.KMH)
            } ?: SpeedUnit.KMH,
            simulatorMode = prefs[Keys.SIMULATOR_MODE] ?: true,
            relayHost = prefs[Keys.RELAY_HOST] ?: "192.168.1.42",
            relayPort = prefs[Keys.RELAY_PORT] ?: 8888,
        )
    }

    suspend fun updateSettings(transform: (AppSettings) -> AppSettings) {
        context.dataStore.edit { prefs ->
            val current = AppSettings(
                displayTheme = prefs[Keys.DISPLAY_THEME]?.let {
                    runCatching { DisplayTheme.valueOf(it) }.getOrDefault(DisplayTheme.GAUGE)
                } ?: DisplayTheme.GAUGE,
                speedUnit = prefs[Keys.SPEED_UNIT]?.let {
                    runCatching { SpeedUnit.valueOf(it) }.getOrDefault(SpeedUnit.KMH)
                } ?: SpeedUnit.KMH,
                simulatorMode = prefs[Keys.SIMULATOR_MODE] ?: true,
                relayHost = prefs[Keys.RELAY_HOST] ?: "192.168.1.42",
                relayPort = prefs[Keys.RELAY_PORT] ?: 8888,
            )
            val updated = transform(current)
            prefs[Keys.DISPLAY_THEME] = updated.displayTheme.name
            prefs[Keys.SPEED_UNIT] = updated.speedUnit.name
            prefs[Keys.SIMULATOR_MODE] = updated.simulatorMode
            prefs[Keys.RELAY_HOST] = updated.relayHost
            prefs[Keys.RELAY_PORT] = updated.relayPort
        }
    }
}
