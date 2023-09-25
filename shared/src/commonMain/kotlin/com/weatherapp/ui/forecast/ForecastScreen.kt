package com.weatherapp.ui.forecast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.weatherapp.ui.forecast.component.ForecastContent
import com.weatherapp.ui.settings.SettingsComponent

@Composable
fun ForecastScreen(
    settingsComponent: SettingsComponent,
    forecastComponent: ForecastComponent,
    onCloseClick: () -> Unit,
) {
    val forecastState by forecastComponent.state.collectAsState()
    val settingsState by settingsComponent.state.collectAsState()

    ForecastContent(
        forecastState,
        settingsState,
        onCloseClick
    )
}