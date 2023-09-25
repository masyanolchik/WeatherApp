package com.weatherapp.data.repository.settings

import com.weatherapp.core.database.dao.SettingsDao
import com.weatherapp.core.model.Settings
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.data.toSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsRepositoryImpl(
    private val settingsDao: SettingsDao
): SettingsRepository {
    override suspend fun getSettings(): Flow<Result<Settings>> {
        return try {
            var settingsList = settingsDao.getSettingsEntity()
            if(settingsList.isEmpty()) {
                settingsDao.insert(Settings().toSettingsEntity())
                settingsList = settingsDao.getSettingsEntity()
            }
            val settingsEntity = settingsList.first()
            // Extension function is unresolved for some reason
            flowOf(Result.success(
                Settings(settingsEntity.tempDifference?.toInt() ?: 0, weatherUnit = if(settingsEntity.weatherUnit?.toInt() == 0) WeatherUnit.METRIC else WeatherUnit.IMPERIAL)
            ))
        } catch (ex: Exception) {
            flowOf(Result.failure(ex))
        }
    }

    override suspend fun updateSettings(settings: Settings): Flow<Result<Settings>> {
        return try {
            settingsDao.update(settings.toSettingsEntity())
            flowOf(Result.success(settings))
        } catch (ex: Exception) {
            flowOf(Result.failure(ex))
        }
    }
}