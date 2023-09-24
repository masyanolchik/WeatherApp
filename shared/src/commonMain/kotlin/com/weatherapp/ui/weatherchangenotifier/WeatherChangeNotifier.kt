package com.weatherapp.ui.weatherchangenotifier

interface WeatherChangeNotifier {
    fun setOnWeatherChangedListener(listener: Listener)

    interface Listener {
        fun onWeatherChanges(text: String)
    }
}