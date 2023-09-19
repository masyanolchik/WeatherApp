package com.weatherapp.core.database.dao

import com.weatherapp.core.database.WeatherDatabase
import comweatherapp.LocationEntity
import kotlinx.coroutines.withContext
import weatherAppDispatchers

class LocationDao(
    private val weatherDatabase: WeatherDatabase
) {

    private val query get() = weatherDatabase.locationEntityQueries

    suspend fun insert(locationEntity: LocationEntity) = withContext(weatherAppDispatchers.io) {
        query.insert(locationEntity)
    }

    suspend fun delete(id: Long) = withContext(weatherAppDispatchers.io) {
        query.delete(id)
    }

    suspend fun getAllLocations() = withContext(weatherAppDispatchers.io) {
        query.selectAll().executeAsList()
    }

    suspend fun getLocationByLatitudeLongitude(lat: String, lon: String) = withContext(weatherAppDispatchers.io) {
        query.selectByLatLon(lat, lon).executeAsOne()
    }

    suspend fun clearTable() = withContext(weatherAppDispatchers.io) {
        getAllLocations().forEach {
            delete(it.id)
        }
    }
}