package com.weatherapp.core.network.error

enum class WeatherError {
    ServiceUnavailable,
    ClientError,
    ServerError,
    ResultRetrievingError,
    UnknownError
}
class WeatherApiException(error: WeatherError) : Exception(
    WEATHER_API_EXCEPTION_MESSAGE_BASE+error.toString()
)

const val WEATHER_API_EXCEPTION_MESSAGE_BASE = "Oh-oh something bad happened:"