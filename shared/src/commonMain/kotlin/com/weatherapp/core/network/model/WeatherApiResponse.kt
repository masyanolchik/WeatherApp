package com.weatherapp.core.network.model

import kotlinx.serialization.SerialInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherApiResponse(
    @SerialName("cod") val code: Int,
    val message: Int,
    val cnt: Int,
    val list: List<NetworkDateForecast>,
    val city: NetworkCity,
)

@Serializable
data class NetworkWeatherMainInfo(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    val pressure: Int,
    val humidity: Int
)

@Serializable
data class NetworkWeatherDescription(
    val main: String,
    val description: String,
    val icon: String
)


@Serializable
data class NetworkWindForecast(
    val speed: Double,
    val deg: Double,
    val gust: Double,
)

@Serializable
data class Clouds(
    val all: Int
)

@Serializable
data class NetworkDateForecast(
    val dt: Long,
    val main: NetworkWeatherMainInfo,
    val weather: List<NetworkWeatherDescription>,
    @SerialName("clouds") val cloudiness: Clouds,
    val wind: NetworkWindForecast,
    val visibility: Int,
    val pop: Double,
)

@Serializable
data class NetworkCityCoordinates(
    val lat: Double,
    val lon: Double,
)
@Serializable
data class NetworkCity(
    val id: Int,
    val name: String,
    val coord: NetworkCityCoordinates,
    val country: String,
    val population: Int,
    @SerialName("timezone") val timeZone: Long,
    val sunrise: Long,
    val sunset: Long
)

