package com.weatherapp.core.database.dao

import com.weatherapp.core.database.WeatherDatabase
import comweatherapp.ForecastEntity
import kotlinx.coroutines.withContext
import weatherAppDispatchers

class ForecastDao(
    private val weatherDatabase: WeatherDatabase
) {

    private val query get() = weatherDatabase.forecastEntityQueries

    suspend fun insert(forecastEntity: ForecastEntity) = withContext(weatherAppDispatchers.io) {
        query.insert(forecastEntity)
    }

    suspend fun getAllForecasts() = withContext(weatherAppDispatchers.io) {
        query.selectAll().executeAsList().map {
            Pair(it,query.getLocationById(it.locationId).executeAsOne())
        }
    }

    suspend fun delete(id: Long) = withContext(weatherAppDispatchers.io) {
        query.delete(id)
    }

    suspend fun clearTable() = withContext(weatherAppDispatchers.io) {
        getAllForecasts().forEach {
            delete(it.first.id)
        }
    }
}