package com.weatherapp.ui.settings.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.weatherapp.core.model.Settings
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.data.repository.settings.SettingsRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import weatherAppDispatchers

class SettingsStoreFactory(private val storeFactory: StoreFactory): KoinComponent {
    private val settingsRepository: SettingsRepository by inject()

    fun create(): SettingsStore =
        object : SettingsStore,
            Store<SettingsStore.Intent, SettingsStore.State, SettingsStore.Label> by storeFactory.create(
                name = "SettingsStore",
                initialState = SettingsStore.State(),
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private inner class ExecutorImpl:
            CoroutineExecutor<SettingsStore.Intent, Unit, SettingsStore.State, Msg, SettingsStore.Label>(weatherAppDispatchers.main) {
        private var lastSettingsData: Settings? = null
        private lateinit var initializationJob: Job
        private var intentJob: Job? = null
        override fun executeAction(action: Unit, getState: () -> SettingsStore.State) {
            dispatch(Msg.Loading)
            initializationJob = scope.launch {
                settingsRepository.getSettings().collectLatest {
                    it.onSuccess { settings ->
                        lastSettingsData = settings
                        dispatch(Msg.Success(settings))
                        publish(SettingsStore.Label.SettingsUpdatedLabel)
                        publish(SettingsStore.Label.NoUpdatesLabel)
                    }.onFailure {
                        dispatch(Msg.Error("Error happened during settings initialization"))
                    }
                }
            }
        }

        override fun executeIntent(
            intent: SettingsStore.Intent,
            getState: () -> SettingsStore.State
        ) {
            intentJob = scope.launch {
                initializationJob.join()
                var lastSettingsDataCopy = lastSettingsData
                if(lastSettingsDataCopy == null) {
                    dispatch(Msg.Error("Error happened during settings initialization"))
                } else {
                    when(intent) {
                        is SettingsStore.Intent.ChangeTempDifference -> {
                            updateSettings(
                                lastSettingsDataCopy
                                    .copy(tempDifference = intent.newTempValue)
                            )
                        }
                        is SettingsStore.Intent.ChangeWeatherUnit -> {
                            updateSettings(
                                lastSettingsDataCopy
                                    .copy(weatherUnit = intent.weatherUnit).also {
                                        lastSettingsData = it
                                    }
                            )
                            publish(SettingsStore.Label.SettingsUpdatedLabel)
                        }
                        SettingsStore.Intent.DecrementTempDifference -> {
                            updateSettings(
                                lastSettingsDataCopy
                                    .copy(tempDifference = lastSettingsDataCopy.tempDifference.dec()).also {
                                        lastSettingsData = it
                                    }
                            )
                        }
                        SettingsStore.Intent.IncrementTempDifference -> {
                            updateSettings(
                                lastSettingsDataCopy
                                    .copy(tempDifference = lastSettingsDataCopy.tempDifference.inc()).also {
                                        lastSettingsData = it
                                    }
                            )
                        }
                    }
                }

            }

        }

        private suspend fun updateSettings(
            settings: Settings
        ) {
            settingsRepository.updateSettings(settings).collectLatest {
                it.onSuccess { settings ->
                    dispatch(Msg.Success(settings))
                }.onFailure {
                    dispatch(Msg.Error("Error updating settings"))
                }
            }
        }
    }

    private sealed class Msg {
        data class Success(val settings: Settings): Msg()

        data object Loading: Msg()

        data class Error(val errorMessage: String): Msg()
    }

    private object ReducerImpl: Reducer<SettingsStore.State, Msg> {
        override fun SettingsStore.State.reduce(msg: Msg): SettingsStore.State {
            return when(msg) {
                is Msg.Success -> copy(
                    currentTempDifference = msg.settings.tempDifference,
                    currentWeatherUnit = msg.settings.weatherUnit,
                    isError = false,
                    errorMessage = "",
                    isLoading = false
                )
                is Msg.Error -> SettingsStore.State(isError = true, errorMessage = msg.errorMessage, isLoading = false)
                Msg.Loading -> SettingsStore.State(isLoading = true)
            }
        }

    }
}