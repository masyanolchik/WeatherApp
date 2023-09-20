package com.weatherapp.data.repository.forecast

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import kotlinx.coroutines.flow.Flow

interface ForecastRepository {
    suspend fun getForecastsForLocation(location: Location, weatherUnit: WeatherUnit): Flow<Result<List<Forecast>>>

    suspend fun getSavedForecastsForLocation(location: Location): Flow<Result<List<Forecast>>>
}