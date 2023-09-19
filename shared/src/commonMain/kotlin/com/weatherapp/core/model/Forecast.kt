package com.weatherapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
    val date: Long,
    val temperature: String,
    val feelsLikeTemperature: String,
    val humidityPercentage: String,
    val weatherTitle: String,
    val weatherDescription: String,
    val weatherIconId: String,
    val cloudinessPercentage: String,
    val windSpeed: String,
    val windDirectionDegrees: String,
    val windGust: String,
    val visibilityMeters: Int,
    val location: Location
) {
    val weatherIconUrl get() =
        "https://openweathermap.org/img/wn/$weatherIconId@4x.png"
}
