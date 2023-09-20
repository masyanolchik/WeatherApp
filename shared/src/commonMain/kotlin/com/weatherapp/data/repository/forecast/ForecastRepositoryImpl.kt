package com.weatherapp.data.repository.forecast

import com.weatherapp.core.database.dao.ForecastDao
import com.weatherapp.core.database.dao.LocationDao
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.core.network.client.WeatherApiClient
import com.weatherapp.data.toForecast
import com.weatherapp.data.toForecastEntity
import com.weatherapp.data.toForecasts
import com.weatherapp.data.toLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ForecastRepositoryImpl(
    private val weatherApiClient: WeatherApiClient,
    private val forecastDao: ForecastDao,
    private val locationDao: LocationDao,
): ForecastRepository {
    override suspend fun getForecastsForLocation(location: Location, weatherUnit: WeatherUnit): Flow<Result<List<Forecast>>> {
        var result: Result<List<Forecast>>
        try {
            forecastDao.clearTable()
            with(location) {
                val forecasts = weatherApiClient
                    .getWeatherForecast(this, weatherUnit)
                    .toForecasts(this)
                val locationEntity = locationDao.getLocationByLatitudeLongitude(
                    latitude,
                    longitude
                )
                forecasts.forEach {
                    forecastDao.insert(it.toForecastEntity(locationEntity))
                }
            }
            result = Result.success(
                forecastDao.getAllForecasts()
                    .map { it.first.toForecast(it.second.toLocation()) }
            )
        } catch (ex: Exception) {
            result = Result.failure(ex)
        }
        return flowOf(result)
    }

    override suspend fun getSavedForecastsForLocation(location: Location): Flow<Result<List<Forecast>>> {
        val result: Result<List<Forecast>> = try {
            val locationEntity = locationDao.getLocationByLatitudeLongitude(
                location.latitude,
                location.longitude
            )
            Result.success(
                forecastDao
                    .getAllForecasts()
                    .filter { it.second == locationEntity }
                    .map { it.first }
                    .map { it.toForecast(locationEntity.toLocation()) }
            )
        } catch (ex: Exception) {
            Result.failure(ex)
        }
        return flowOf(result)
    }
}