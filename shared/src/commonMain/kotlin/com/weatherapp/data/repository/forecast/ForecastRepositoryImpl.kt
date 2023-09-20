package com.weatherapp.data.repository.forecast

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import kotlinx.coroutines.flow.Flow

class ForecastRepositoryImpl: ForecastRepository {
    override fun getForecastsForLocation(location: Location): Flow<Result<List<Forecast>>> {
        TODO("Not yet implemented")
    }

    override fun getSavedForecastsForLocation(location: Location): Flow<Result<List<Forecast>>> {
        TODO("Not yet implemented")
    }
}