package com.weatherapp.core.network

import com.weatherapp.core.BuildKonfig
import com.weatherapp.core.model.WeatherUnit

object NetworkConstants {
    const val baseUrl = "http://api.openweathermap.org/"
    val apiKey: String = BuildKonfig.API_KEY

    object LocationApi {
        private val tailRequest = "&limit=5&appid=$apiKey"
        private const val routeDirect = baseUrl + "geo/1.0/direct?"
        private const val routeZip = baseUrl + "geo/1.0/zip?"
        val byQuery: (String) -> String = { query -> routeDirect+"q=$query$tailRequest" }
        val byZip: (String) -> String = { zip -> routeZip+"zip=$zip$tailRequest" }
    }

    object WeatherForecastApi {
        private const val route = baseUrl + "data/2.5/forecast?"

        val byCoordinates: (String, String, WeatherUnit) -> String = { latitude, longitude, unit ->
            val weatherUnitString = when(unit) {
                WeatherUnit.IMPERIAL -> "imperial"
                WeatherUnit.METRIC -> "metric"
            }
            route + "units=$weatherUnitString&lat=$latitude&lon=$longitude&appId=$apiKey"
        }
    }
}