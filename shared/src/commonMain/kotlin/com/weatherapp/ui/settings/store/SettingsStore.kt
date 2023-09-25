package com.weatherapp.ui.settings.store

import com.arkivanov.mvikotlin.core.store.Store
import com.weatherapp.core.model.WeatherUnit

interface SettingsStore: Store<SettingsStore.Intent, SettingsStore.State, Nothing> {
    sealed class Intent {
        object IncrementTempDifference
        object DecrementTempDifference
        data class ChangeTempDifference(val newTempValue: Int)
        data class ChangeWeatherUnit(val weatherUnit: WeatherUnit)
    }

    data class State(
        val currentTempDifference: Int = 0,
        val currentWeatherUnit: WeatherUnit = WeatherUnit.METRIC,
    )
}