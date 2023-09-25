package com.weatherapp.core.database.dao

import com.weatherapp.core.database.WeatherDatabase
import comweatherapp.SettingsEntity
import kotlinx.coroutines.withContext
import weatherAppDispatchers

class SettingsDao(
    private val weatherDatabase: WeatherDatabase
) {
    private val query get() = weatherDatabase.settingsEntityQueries

    suspend fun insert(settingsEntity: SettingsEntity) = withContext(weatherAppDispatchers.io) {
        query.insert(settingsEntity)
    }

    suspend fun getSettingsEntity() = withContext(weatherAppDispatchers.io) {
        query.getAll().executeAsList()
    }

    suspend fun update(settingsEntity: SettingsEntity) = withContext(weatherAppDispatchers.io) {
        query.update(
            settingsEntity.tempDifference,
            settingsEntity.weatherUnit,
            settingsEntity.id,
        )
    }


}