package com.weatherapp.core.network.error

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.withContext
import io.ktor.utils.io.errors.IOException
import weatherAppDispatchers

suspend inline fun <reified T> handleErrors(
    crossinline response: suspend () -> HttpResponse
): T = withContext(weatherAppDispatchers.io) {

    val result = try {
        response()
    } catch(e: IOException) {
        throw WeatherApiException(WeatherError.ServiceUnavailable)
    }

    when(result.status.value) {
        in 200..299 -> Unit
        in 400..499 -> throw WeatherApiException(WeatherError.ClientError)
        500 -> throw WeatherApiException(WeatherError.ServerError)
        else -> throw WeatherApiException(WeatherError.UnknownError)
    }

    return@withContext try {
        result.body()
    } catch(e: Exception) {
        throw WeatherApiException(WeatherError.ResultRetrievingError)
    }

}