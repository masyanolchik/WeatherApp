package com.weatherapp.data.repository.forecast

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    fun getForecastsForLocation(location: Location): Flow<Result<List<Forecast>>>

    fun getSavedForecastsForLocation(location: Location): Flow<Result<List<Forecast>>>
}