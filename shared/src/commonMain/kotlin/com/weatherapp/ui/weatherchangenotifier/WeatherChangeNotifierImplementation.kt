package com.weatherapp.ui.weatherchangenotifier

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.ui.refresher.ForecastRefresher

class WeatherChangeNotifierImplementation(
    private val forecastRefresher: ForecastRefresher
) :
    WeatherChangeNotifier {
    private var onWeatherChangedListener: WeatherChangeNotifier.Listener? = null
    private val lastSuccessfulResult = mutableListOf<Forecast>()

    init {
        forecastRefresher.addListener(object : ForecastRefresher.Listener {
            override fun onRefreshStarted(location: Location) {}

            override fun onRefreshFinished(result: Result<List<Forecast>>) {
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
                            val firstSignificantlyChangedForecast = changedList.first {
                                val newForecast = it.first
                                val oldForecast = it.second

                                oldForecast.weatherDescription != newForecast.weatherDescription || kotlin.math.abs(
                                    oldForecast.temperature - newForecast.temperature
                                ) >= 5
                            }.first
                            onWeatherChangedListener?.onWeatherChanges("Weather forecast for ${firstSignificantlyChangedForecast.location.name} has changed. Now it's ${firstSignificantlyChangedForecast.temperature} and ${firstSignificantlyChangedForecast.weatherDescription}")
                        }


                    }
                    lastSuccessfulResult.clear()
                    lastSuccessfulResult.addAll(forecasts)
                }
            }

        })
    }

    override fun setOnWeatherChangedListener(listener: WeatherChangeNotifier.Listener) {
        onWeatherChangedListener = listener
    }
}