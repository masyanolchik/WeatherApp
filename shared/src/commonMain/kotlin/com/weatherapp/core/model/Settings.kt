package com.weatherapp.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Settings(
    val tempDifference: Int = 0,
    val weatherUnit: WeatherUnit = WeatherUnit.METRIC,
)
