package com.weatherapp.data.repository.settings

import androidx.compose.ui.text.font.FontVariation
import com.weatherapp.core.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun getSettings(): Flow<Result<Settings>>
    suspend fun updateSettings(settings: Settings): Flow<Result<Settings>>
}