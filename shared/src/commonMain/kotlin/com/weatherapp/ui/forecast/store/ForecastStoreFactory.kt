package com.weatherapp.ui.forecast.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.data.repository.forecast.ForecastRepository
import com.weatherapp.data.repository.location.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import weatherAppDispatchers

class ForecastStoreFactory(private val storeFactory: StoreFactory): KoinComponent {
    private val locationRepository: LocationRepository by inject()
    private val forecastRepository: ForecastRepository by inject()

    fun create(): ForecastStore =
        object : ForecastStore,
            Store<ForecastStore.Intent, ForecastStore.State, Nothing> by storeFactory.create(
                name = "ForecastStore",
                initialState = ForecastStore.State(),
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

     private sealed class Msg {
        data class Error(val errorMessage: String): Msg()
        data object Loading: Msg()

        data class Success(val forecasts: List<Forecast>, val localDateTime: LocalDateTime): Msg()
     }

    private inner class ExecutorImpl:
        CoroutineExecutor<ForecastStore.Intent, Unit, ForecastStore.State, Msg, Nothing>(weatherAppDispatchers.main) {

        private val locationWithDateFlow = MutableStateFlow<Pair<Location, LocalDateTime>?>(null)
        private val forecastProcessFlow = locationWithDateFlow
            .filterNotNull()
            .onEach {
                dispatch(Msg.Loading)
            }
            .flatMapLatest {
                forecastRepository.getSavedForecastsForLocation(it.first)
            }
            .onEach {
                val currentLocalDateTime = locationWithDateFlow.value?.second
                if(currentLocalDateTime != null) {
                    it.onSuccess { forecasts ->
                        val forecastsForCurrentDate = forecasts
                            .filter { forecast ->
                                forecast.getLocalDateTime().dayOfYear == currentLocalDateTime.dayOfYear
                            }
                            .sortedBy { forecast -> forecast.date }
                        dispatch(Msg.Success(forecastsForCurrentDate, currentLocalDateTime))
                    }.onFailure {
                        dispatch(Msg.Error("Error loading "))
                    }
                } else {
                    dispatch(Msg.Error("Error loading "))
                }
            }
            .shareIn(scope, SharingStarted.WhileSubscribed())

        init {
            forecastProcessFlow.launchIn(scope)
        }

        override fun executeIntent(
            intent: ForecastStore.Intent,
            getState: () -> ForecastStore.State
        ) {
            when(intent) {
                is ForecastStore.Intent.ShowForecastForLocationForDay -> {
                    locationWithDateFlow.update {
                        Pair(intent.location, intent.localDateTime)
                    }
                }
            }
        }
    }

    private object ReducerImpl: Reducer<ForecastStore.State, Msg> {
        override fun ForecastStore.State.reduce(msg: Msg): ForecastStore.State {
            return when(msg) {
                is Msg.Error -> copy(isError = true, errorText = msg.errorMessage)
                Msg.Loading -> copy(isLoading = true)
                is Msg.Success -> copy(forecasts = msg.forecasts, dateTime = msg.localDateTime,isError = false, isLoading = false)
            }
        }

    }
}