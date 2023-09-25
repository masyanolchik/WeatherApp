package com.weatherapp.ui.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.weatherapp.core.model.WeatherUnit

interface SettingsStore: Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label> {
    sealed class Intent {
        data object IncrementTempDifference: Intent()
        data object DecrementTempDifference: Intent()
        data class ChangeTempDifference(val newTempValue: Int): Intent()
        data class ChangeWeatherUnit(val weatherUnit: WeatherUnit): Intent()
    }

    data class State(
        val currentTempDifference: Int = 0,
        val currentWeatherUnit: WeatherUnit = WeatherUnit.METRIC,
        val isError: Boolean = false,
        val errorMessage: String = "",
        val isLoading: Boolean = false,
    )

    sealed class Label {
        data object SettingsUpdatedLabel: Label()
        data object NoUpdatesLabel: Label()
    }
}