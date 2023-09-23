package com.weatherapp.core.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import java.time.format.TextStyle
import java.util.Locale

@Serializable
data class Forecast(
    val date: Long,
    val temperature: Int,
    val feelsLikeTemperature: Int,
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

    fun getLocalDateTime() = Instant.fromEpochMilliseconds(date*1000)
        .toLocalDateTime(TimeZone.currentSystemDefault())

    fun getReadableDayOfWeekRepresentation(): String {
        val actualDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val forecastDateTime = getLocalDateTime()
        return if(actualDateTime.dayOfYear == forecastDateTime.dayOfYear) {
            "Today"
        } else {
            forecastDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        }
    }
}
