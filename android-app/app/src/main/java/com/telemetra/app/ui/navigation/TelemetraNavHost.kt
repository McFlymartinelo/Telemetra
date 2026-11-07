package com.telemetra.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.telemetra.app.data.model.DisplayTheme
import com.telemetra.app.data.model.SpeedUnit
import com.telemetra.app.ui.screens.cards.CardsThemeScreen
import com.telemetra.app.ui.screens.connection.ConnectionScreen
import com.telemetra.app.ui.screens.gauge.GaugeThemeScreen
import com.telemetra.app.ui.screens.settings.SettingsScreen
import com.telemetra.app.ui.viewmodel.TelemetraViewModel

object Routes {
    const val DASHBOARD = "dashboard"
    const val SETTINGS = "settings"
    const val CONNECTION = "connection"
}

@Composable
fun TelemetraNavHost(viewModel: TelemetraViewModel = viewModel()) {
    val navController = rememberNavController()
    val telemetry by viewModel.telemetry.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val useMph = settings.speedUnit == SpeedUnit.MPH

    NavHost(navController = navController, startDestination = Routes.DASHBOARD) {
        composable(Routes.DASHBOARD) {
            when (settings.displayTheme) {
                DisplayTheme.GAUGE -> GaugeThemeScreen(
                    telemetry = telemetry,
                    connectionStatus = connectionStatus,
                    useMph = useMph,
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                )
                DisplayTheme.CARDS -> CardsThemeScreen(
                    telemetry = telemetry,
                    connectionStatus = connectionStatus,
                    useMph = useMph,
                    onSettingsClick = { navController.navigate(Routes.SETTINGS) },
                )
            }
        }

        composable(Routes.SETTINGS) {
            SettingsScreen(
                settings = settings,
                onThemeChange = viewModel::setDisplayTheme,
                onSpeedUnitChange = viewModel::setSpeedUnit,
                onSimulatorToggle = { viewModel.setSimulatorMode(!settings.simulatorMode) },
                onConnectionClick = { navController.navigate(Routes.CONNECTION) },
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.CONNECTION) {
            ConnectionScreen(
                connectionStatus = connectionStatus,
                relayHost = settings.relayHost,
                relayPort = settings.relayPort,
                simulatorMode = settings.simulatorMode,
                onConnect = viewModel::connect,
                onDisconnect = viewModel::disconnect,
                onEnableSimulator = { viewModel.setSimulatorMode(true) },
                onBack = { navController.popBackStack() },
            )
        }
    }
}
