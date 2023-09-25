package com.weatherapp.ui.weatherchangenotifier

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.ui.refresher.ForecastRefresher
import com.weatherapp.ui.settings.SettingsComponent
import com.weatherapp.ui.settings.store.SettingsStore
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter

class WeatherChangeNotifierImplementation(
    private val forecastRefresher: ForecastRefresher,
    private val settingsComponent: SettingsComponent
) :
    WeatherChangeNotifier {
    private var onWeatherChangedListener: WeatherChangeNotifier.Listener? = null
    private val lastSuccessfulResult = mutableListOf<Forecast>()
    private val settingsState: StateFlow<SettingsStore.State>
        get() = settingsComponent.state

    init {
        forecastRefresher.addListener(object : ForecastRefresher.Listener {
            override fun onRefreshStarted(location: Location) {}

            override fun onRefreshFinished(result: Result<List<Forecast>>) {
                val settings = settingsState.value
                if(!settings.isError || !settings.isLoading) {

                    result.onSuccess { forecasts ->
                        if (lastSuccessfulResult.isNotEmpty() && forecasts != lastSuccessfulResult) {
                            val changedList = mutableListOf<Pair<Forecast, Forecast>>()
                            val firstTwoDays = forecasts.take(16)
                            lastSuccessfulResult.forEach { oldForecast ->
                                firstTwoDays.forEach findingPair@{ newForecast ->
                                    if (newForecast.date == oldForecast.date && newForecast != oldForecast) {
                                        changedList.add(Pair(newForecast, oldForecast))
                                        return@findingPair
                                    }
                                }
                            }

                            if (changedList.isNotEmpty()) {
                                val firstSignificantlyChangedForecast = changedList.firstOrNull {
                                    val newForecast = it.first
                                    val oldForecast = it.second

                                    oldForecast.weatherDescription != newForecast.weatherDescription || kotlin.math.abs(
                                        oldForecast.temperature - newForecast.temperature
                                    ) >= kotlin.math.abs(settings.currentTempDifference)
                                }?.first
                                if(firstSignificantlyChangedForecast != null) {
                                    onWeatherChangedListener?.onWeatherChanges("Weather forecast for ${firstSignificantlyChangedForecast.location.name} has changed. Now it's ${firstSignificantlyChangedForecast.temperature} ${if(settings.currentWeatherUnit == WeatherUnit.METRIC) "C" else "F"} and ${firstSignificantlyChangedForecast.weatherDescription}")
                                }
                            }


                        }
                        lastSuccessfulResult.clear()
                        lastSuccessfulResult.addAll(forecasts)
                    }
                }
            }

        })
    }

    override fun setOnWeatherChangedListener(listener: WeatherChangeNotifier.Listener) {
        onWeatherChangedListener = listener
    }
}