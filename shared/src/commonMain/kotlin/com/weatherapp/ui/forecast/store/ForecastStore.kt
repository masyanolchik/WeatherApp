package com.weatherapp.ui.forecast.store

import com.arkivanov.mvikotlin.core.store.Store
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

interface ForecastStore: Store<ForecastStore.Intent, ForecastStore.State, Nothing> {

    sealed class Intent {
        data class ShowForecastForLocationForDay(
            val location: Location,
            val localDateTime: LocalDateTime
        ): Intent()
    }

    data class State(
        val forecasts: List<Forecast> = emptyList(),
        val dateTime: LocalDateTime = Clock.System.now()
            .toLocalDateTime(TimeZone.currentSystemDefault()),
        val isLoading: Boolean = false,
        val isError: Boolean = false,
        val errorText: String = ""
    )
}