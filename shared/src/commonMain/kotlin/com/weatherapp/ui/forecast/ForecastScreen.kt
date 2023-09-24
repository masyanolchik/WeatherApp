package com.weatherapp.ui.forecast

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.weatherapp.ui.forecast.component.ForecastContent

@Composable
fun ForecastScreen(
    forecastComponent: ForecastComponent,
    onCloseClick: () -> Unit,
) {
    val forecastState by forecastComponent.state.collectAsState()

    ForecastContent(
        forecastState,
        onCloseClick
    )
}