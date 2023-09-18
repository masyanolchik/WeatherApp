package com.weatherapp.core.network.client

import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.core.network.NetworkConstants
import com.weatherapp.core.network.error.handleErrors
import com.weatherapp.core.network.model.LocationQueryApiResponse
import com.weatherapp.core.network.model.LocationZipApiResponse
import com.weatherapp.core.network.model.WeatherApiResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import io.ktor.http.ContentType
import io.ktor.http.contentType

class WeatherApiClient(
    private val httpClient: HttpClient
) {

    suspend fun getLocationByQuery(query: String): Flow<List<LocationQueryApiResponse>> {
        return flowOf(
            handleErrors {
                httpClient.get(NetworkConstants.LocationApi.byQuery(query)) {
                    contentType(ContentType.Application.Json)
                }
            }
        )
    }

    suspend fun getLocationByZipCode(zip: String): Flow<LocationZipApiResponse> {
        return flowOf(
            handleErrors {
                httpClient.get(NetworkConstants.LocationApi.byZip(zip)) {
                    contentType(ContentType.Application.Json)
                }
            }
        )
    }

    suspend fun getWeatherForecast(
        location: Location,
        weatherUnit: WeatherUnit
    ): Flow<WeatherApiResponse> {
        return flowOf(
            handleErrors {
                httpClient.get(
                    NetworkConstants.WeatherForecastApi.byCoordinates(
                        location.latitude,
                        location.longitude,
                        weatherUnit
                    )
                )
            }
        )
    }
}