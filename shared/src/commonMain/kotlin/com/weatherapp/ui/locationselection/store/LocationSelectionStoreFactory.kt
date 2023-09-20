package com.weatherapp.ui.locationselection.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.weatherapp.core.model.Location
import com.weatherapp.data.repository.location.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.test.KoinTest
import org.koin.test.inject
import weatherAppDispatchers

internal class LocationSelectionStoreFactory(private val storeFactory: StoreFactory): KoinTest {
    private val locationRepository: LocationRepository by inject()

    fun create(): LocationSelectionStore =
        object: LocationSelectionStore, Store<LocationSelectionStore.Intent, LocationSelectionStore.State, LocationSelectionStore.Label> by storeFactory.create(
            name = "LocationSelectionStore",
            initialState = LocationSelectionStore.State.FoundLocations("", emptyList()),
            bootstrapper = SimpleBootstrapper(Unit),
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed class Msg {
        data class ListLoading(val query: String): Msg()

        data class Error(val errorMessage: String, val query: String): Msg()

        data class ListLoaded(val searchQuery: String, val locations: List<Location>): Msg()
    }

    private inner class ExecutorImpl():
        CoroutineExecutor<LocationSelectionStore.Intent,
                Unit,
                LocationSelectionStore.State,
                Msg,
                LocationSelectionStore.Label>
            (weatherAppDispatchers.main) {
        private val saveCurrentLocationFlow = MutableStateFlow<Location?>(null)
        private val queryFlow = MutableStateFlow("")

        init {
            launchCurrentLocationFlow()
            launchQueryFlow()
        }

        private fun launchCurrentLocationFlow() {
            saveCurrentLocationFlow
                .filterNotNull()
                .distinctUntilChanged()
                .flowOn(weatherAppDispatchers.io)
                .flatMapLatest {  location ->
                    locationRepository.saveSelectedLocation(location)
                }
                .onEach {
                    it.onSuccess {
                        saveCurrentLocationFlow.value?.let { location ->
                            publish(LocationSelectionStore.Label.CurrentLocationChosen(location))
                        }
                    }.onFailure {
                        saveCurrentLocationFlow.value?.let { location ->
                            publish(LocationSelectionStore.Label.CurrentLocationChosingError(location, "Failed to save the chosen location"))
                        }
                    }
                }
                .launchIn(scope)
        }

        private fun launchQueryFlow() {
            queryFlow
                .debounce(300)
                .filterNot { query -> query.isNotEmpty() }
                .distinctUntilChanged()
                .onEach {
                    dispatch(Msg.ListLoading(it))
                }
                .flowOn(weatherAppDispatchers.io)
                .flatMapLatest {
                    locationRepository.getLocations(it)
                }.onEach {
                    it.onSuccess { list ->
                        dispatch(Msg.ListLoaded(queryFlow.value, list))
                    }.onFailure { _ ->
                        dispatch(Msg.Error("Error loading locations",queryFlow.value))
                    }
                }.launchIn(scope)
        }

        override fun executeAction(action: Unit, getState: () -> LocationSelectionStore.State) {
            scope.launch {
                dispatch(Msg.ListLoading(""))
                val savedLocationFlow = locationRepository.getSavedSelectedLocation()
                savedLocationFlow.collectLatest {
                    it.onSuccess { location ->
                        publish(LocationSelectionStore.Label.CurrentLocationChosen(location))
                        dispatch(Msg.ListLoading(location.toString()))
                    }
                }
            }
        }

        override fun executeIntent(
            intent: LocationSelectionStore.Intent,
            getState: () -> LocationSelectionStore.State
        ) =
            when(intent) {
                is LocationSelectionStore.Intent.ChoseCurrentLocation -> {
                    saveCurrentLocationFlow.value = intent.location
                }
                is LocationSelectionStore.Intent.EnterSearchQueryIntent -> {
                    queryFlow.value = intent.searchQuery
                }
            }
    }

    private object ReducerImpl: Reducer<LocationSelectionStore.State, Msg> {
        override fun LocationSelectionStore.State.reduce(msg: Msg): LocationSelectionStore.State =
            when(msg) {
                is Msg.ListLoading -> LocationSelectionStore.State.Loading(msg.query)
                is Msg.Error -> LocationSelectionStore.State.Error(msg.query, msg.errorMessage)
                is Msg.ListLoaded -> LocationSelectionStore.State.FoundLocations(msg.searchQuery, msg.locations)
            }
    }

}