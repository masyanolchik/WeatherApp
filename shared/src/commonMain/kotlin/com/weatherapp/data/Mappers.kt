package com.weatherapp.data

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.core.model.Settings
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.core.network.model.LocationQueryApiResponse
import com.weatherapp.core.network.model.LocationZipApiResponse
import com.weatherapp.core.network.model.WeatherApiResponse
import comweatherapp.ForecastEntity
import comweatherapp.LocationEntity
import comweatherapp.SettingsEntity

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

fun Location.toLocationEntity(isSelected: Boolean) = LocationEntity(
    id = 0L,
    name = name,
    chosen = if(isSelected) 1L else 0L,
    latitude = latitude,
    longtitude = longitude,
    country = country,
    state = state,
    zip = zip
)

fun LocationEntity.toLocation() = Location(
    name = name,
    latitude = latitude,
    longitude = longtitude,
    country = country,
    state = state ?: "",
    zip = zip ?: ""
)

fun WeatherApiResponse.toForecasts(location: Location) = buildList {
    addAll(list.map {
        Forecast(
            date = it.dt,
            temperature = it.main.temp.toInt(),
            temperatureMin = it.main.tempMin.toInt(),
            temperatureMax = it.main.tempMax.toInt(),
            feelsLikeTemperature = it.main.feelsLike.toInt(),
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

fun Forecast.toForecastEntity(locationEntity: LocationEntity) = ForecastEntity(
    id = 0,
    date = date,
    temperature = temperature.toString(),
    temperatureMin = temperatureMin.toString(),
    temperatureMax = temperatureMax.toString(),
    feelsLikeTemperature = feelsLikeTemperature.toString(),
    humidityPercentage = humidityPercentage,
    weatherTitle = weatherTitle,
    weatherDescription = weatherDescription,
    weatherIconId = weatherIconId,
    cloudinessPercentage = cloudinessPercentage,
    windSpeed = windSpeed,
    windDirectionDegrees = windDirectionDegrees,
    windGust = windGust,
    visibilityMeters = visibilityMeters.toLong(),
    locationId = locationEntity.id
)

fun ForecastEntity.toForecast(location: Location) = Forecast(
    date = date,
    temperature = temperature.toInt(),
    feelsLikeTemperature = feelsLikeTemperature.toInt(),
    temperatureMin = temperatureMin.toInt(),
    temperatureMax = temperatureMax.toInt(),
    humidityPercentage = humidityPercentage,
    weatherTitle = weatherTitle,
    weatherDescription = weatherDescription,
    weatherIconId = weatherIconId,
    cloudinessPercentage = cloudinessPercentage,
    windSpeed = windSpeed,
    windDirectionDegrees = windDirectionDegrees,
    windGust = windGust,
    visibilityMeters = visibilityMeters.toInt(),
    location = location
)

fun Settings.toSettingsEntity() =
    SettingsEntity(
        id = 1,
        tempDifference = tempDifference.toLong(),
        weatherUnit = if(weatherUnit == WeatherUnit.METRIC) 0 else 1
    )

fun SettingsEntity.toSettings() =
    Settings(
        tempDifference = tempDifference?.toInt() ?: 0,
        weatherUnit = if(weatherUnit?.toInt() == 0) WeatherUnit.METRIC else WeatherUnit.IMPERIAL
    )