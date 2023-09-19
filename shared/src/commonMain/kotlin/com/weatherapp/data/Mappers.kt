package com.weatherapp.data

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.core.network.model.LocationQueryApiResponse
import com.weatherapp.core.network.model.LocationZipApiResponse
import com.weatherapp.core.network.model.WeatherApiResponse
import comweatherapp.LocationEntity

fun LocationQueryApiResponse.toLocation() = Location(
    name = name,
    latitude = lat.toString(),
    longitude = lon.toString(),
    country = country,
    state = state,
    zip = ""
)

fun LocationZipApiResponse.toLocation() = Location(
    name = name,
    latitude = lat.toString(),
    longitude = lon.toString(),
    country = country,
    state = "",
    zip = zip
)

fun Location.toLocationEntity() = LocationEntity(
    id = 0L,
    name = name,
    latitude = latitude,
    longtitude = longitude,
    country = country,
    state = state,
    zip = zip
)

fun WeatherApiResponse.toForecasts() = buildList<Forecast> {
    val location = Location(
        name = city.name,
        latitude = city.coord.lat.toString(),
        longitude = city.coord.lon.toString(),
        country = city.country,
        state = "",
        zip = ""
    )

    addAll(list.map {
        Forecast(
            date = it.dt,
            temperature = it.main.temp.toString(),
            feelsLikeTemperature = it.main.feelsLike.toString(),
            humidityPercentage = it.main.humidity.toString(),
            weatherTitle = it.weather.first().main,
            weatherDescription = it.weather.first().description,
            weatherIconId = it.weather.first().icon,
            cloudinessPercentage = it.cloudiness.all.toString(),
            windSpeed = it.wind.speed.toString(),
            windDirectionDegrees = it.wind.deg.toString(),
            windGust = it.wind.gust.toString(),
            visibilityMeters = it.visibility,
            location = location,
        )
    })
}