package com.weatherapp.data.repository.settings

import com.weatherapp.core.database.dao.SettingsDao
import com.weatherapp.core.model.Settings
import com.weatherapp.data.toSettings
import com.weatherapp.data.toSettingsEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class SettingsRepositoryImpl(
    private val settingsDao: SettingsDao
): SettingsRepository {
    override suspend fun getSettings(): Flow<Result<Settings>> {
        return try {
            flowOf(Result.success(settingsDao.getSettingsEntity().toSettings()))
        } catch (ex: Exception) {
            flowOf(Result.failure(ex))
        }
    }

    override suspend fun updateSettings(settings: Settings): Flow<Result<Unit>> {
        return try {
            settingsDao.update(settings.toSettingsEntity())
            flowOf(Result.success(Unit))
        } catch (ex: Exception) {
            flowOf(Result.failure(ex))
        }
    }
}