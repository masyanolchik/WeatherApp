package com.weatherapp.data.repository.location

import com.weatherapp.core.database.dao.LocationDao
import com.weatherapp.core.model.Location
import com.weatherapp.core.network.client.WeatherApiClient
import com.weatherapp.data.toLocation
import com.weatherapp.data.toLocationEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class LocationRepositoryImpl(
    private val weatherApiClient: WeatherApiClient,
    private val locationDao: LocationDao
): LocationRepository {
    override suspend fun getLocations(query: String): Flow<Result<List<Location>>> {
        var result: Result<List<Location>>
        try {
            val locationQueryList =
                weatherApiClient.getLocationsByQuery(query)
                    .map {
                        it.toLocation()
                    }
            locationDao.clearTable()
            locationQueryList.forEach {
                locationDao.insert(it.toLocationEntity(false))
            }

            if(locationQueryList.isEmpty()) {
                val locationZip =
                    weatherApiClient.getLocationsByZipCode(query).toLocation()
                locationDao.insert(locationZip.toLocationEntity(false))
            }
            val locationsAll = locationDao.getAllLocations()
            result = Result.success(
                buildList {
                    addAll(locationsAll.map { it.toLocation() })
                }
            )
        } catch (ex: Exception) {
            result = Result.failure(ex)
        }
        return flowOf(result)
    }

    override suspend fun saveSelectedLocation(location: Location): Flow<Result<Unit>> {
        return flowOf(
            try {
                locationDao.update(location.toLocationEntity(true))
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        )
    }

    override suspend fun getSavedSelectedLocation(): Flow<Result<Location>> {
        return flowOf (
            try {
                Result.success(locationDao.getSelectedLocation().toLocation())
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        )
    }
}