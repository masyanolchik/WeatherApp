package com.weatherapp.ui.weatherchangenotifier

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.ui.refresher.ForecastRefresher

class WeatherChangeNotifierImplementation(private val forecastRefresher: ForecastRefresher) :
    WeatherChangeNotifier {
    private var onWeatherChangedListener: WeatherChangeNotifier.Listener? = null
    private val lastSuccessfulResult = mutableListOf<Forecast>()

    init {
        forecastRefresher.addListener(object : ForecastRefresher.Listener {
            override fun onRefreshStarted(location: Location) {}

            override fun onRefreshFinished(result: Result<List<Forecast>>) {
                result.onSuccess { forecasts ->
                    if (forecasts != lastSuccessfulResult) {
                        val firstNotEqualForecast =
                            forecasts.first { !lastSuccessfulResult.contains(it) }
                        onWeatherChangedListener?.onWeatherChanges("Weather forecast for ${firstNotEqualForecast.location.name} has changed. Now it's ${firstNotEqualForecast.temperature} and ${firstNotEqualForecast.weatherDescription}")
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