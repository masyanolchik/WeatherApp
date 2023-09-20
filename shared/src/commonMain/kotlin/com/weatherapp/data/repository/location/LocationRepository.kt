package com.weatherapp.data.repository.location

import com.weatherapp.core.model.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    suspend fun getLocations(query:String): Flow<Result<List<Location>>>

    suspend fun saveSelectedLocation(location: Location): Flow<Result<Unit>>

    suspend fun getSavedSelectedLocation(): Flow<Result<Location>>
}