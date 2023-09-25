package com.weatherapp.ui.locationselection.store

import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.SimpleBootstrapper
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.data.repository.forecast.ForecastRepository
import com.weatherapp.data.repository.location.LocationRepository
import com.weatherapp.ui.refresher.ForecastRefresher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import weatherAppDispatchers
import kotlin.math.max

internal class LocationSelectionStoreFactory(private val storeFactory: StoreFactory) : KoinComponent {
    private val locationRepository: LocationRepository by inject()
    private val forecastRepository: ForecastRepository by inject()
    private val refresher: ForecastRefresher by inject()

    fun create(): LocationSelectionStore =
        object : LocationSelectionStore,
            Store<LocationSelectionStore.Intent, LocationSelectionStore.State, LocationSelectionStore.Label> by storeFactory.create(
                name = "LocationSelectionStore",
                initialState = LocationSelectionStore.State.FoundLocations("", emptyList(), false),
                bootstrapper = SimpleBootstrapper(Unit),
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed class Msg {
        data class ListLoading(val query: String) : Msg()

        data class SelectedLocation(
            val locationTitle: String,
            val isLoadingForecasts: Boolean = false,
            val isErrorLoadingForecasts: Boolean = false,
            val locationForecasts: List<Forecast> = emptyList()
        ) : Msg()

        data class Error(val errorMessage: String, val query: String) : Msg()

        data class ListLoaded(val searchQuery: String, val locations: List<Location>) : Msg()
    }

    private inner class ExecutorImpl() :
        CoroutineExecutor<LocationSelectionStore.Intent,
                Unit,
                LocationSelectionStore.State,
                Msg,
                LocationSelectionStore.Label>
            (weatherAppDispatchers.main) {
        private var refresherListener: ForecastRefresher.Listener? = null

        private val queryFlow = MutableStateFlow("")
        private val queryProcessFlow = queryFlow
            .debounce(700)
            .filter { query ->
                query.isNotEmpty()
            }
            .distinctUntilChanged()
            .flatMapLatest {
                locationRepository.getLocations(it)
            }
            .onEach {
                it.onSuccess { list ->
                    dispatch(Msg.ListLoaded(queryFlow.value, list))
                }.onFailure { _ ->
                    dispatch(Msg.Error("Error loading locations", queryFlow.value))
                }
            }
            .shareIn(scope, SharingStarted.WhileSubscribed())

        private val currentLocationFlow = MutableStateFlow<Location?>(null)
        private val saveCurrentLocationFlow = currentLocationFlow
            .filterNotNull()
            .flatMapLatest { location ->
                locationRepository.saveSelectedLocation(location)
            }
            .onEach {
                it.onSuccess {
                    currentLocationFlow.value?.let { location ->
                        publish(LocationSelectionStore.Label.CurrentLocationChosen(location))
                        dispatch(Msg.SelectedLocation("${location.name}, ${location.state}${location.zip}, ${location.country}"))
                    }
                }.onFailure {
                    currentLocationFlow.value?.let { location ->
                        publish(
                            LocationSelectionStore.Label.CurrentLocationChoosingError(
                                location,
                                "Failed to save the chosen location"
                            )
                        )
                    }
                }
            }
            .shareIn(scope, SharingStarted.WhileSubscribed())


        init {
            saveCurrentLocationFlow.launchIn(scope)
            queryProcessFlow.launchIn(scope)
        }

        override fun executeAction(action: Unit, getState: () -> LocationSelectionStore.State) {
            dispatch(Msg.SelectedLocation(""))
            scope.launch {
                locationRepository.getSavedSelectedLocation()
                    .collectLatest {
                        it.onSuccess { location ->
                            publish(LocationSelectionStore.Label.CurrentLocationChosen(location))
                        }
                    }
            }
        }

        override fun executeIntent(
            intent: LocationSelectionStore.Intent,
            getState: () -> LocationSelectionStore.State
        ) =
            when (intent) {
                is LocationSelectionStore.Intent.ChoseCurrentLocation -> {
                    currentLocationFlow.update {
                        intent.location
                    }
                }

                is LocationSelectionStore.Intent.EnterSearchQueryIntent -> {
                    queryFlow.update {
                        intent.searchQuery
                    }
                }

                is LocationSelectionStore.Intent.ShowForecastsForLocation -> {
                    refresherListener?.let {
                        refresher.removeListener(it)
                    }
                    refresherListener = object : ForecastRefresher.Listener {
                        override fun onRefreshStarted(location: Location) {
                            dispatch(
                                Msg.SelectedLocation(
                                    "${location.name}, ${location.state}${location.zip}, ${location.country}",
                                    isLoadingForecasts = true
                                )
                            )
                        }

                        override fun onRefreshFinished(result: Result<List<Forecast>>) {
                            result.onSuccess { threeHourForecasts ->
                                dispatchForecast(intent.location, threeHourForecasts)
                            }.onFailure { _ ->
                                scope.launch {
                                    forecastRepository.getSavedForecastsForLocation(intent.location)
                                        .collectLatest { resultSaved ->
                                            resultSaved.onSuccess { threeHourForecast ->
                                                dispatchForecast(intent.location, threeHourForecast)
                                            }.onFailure {
                                                dispatch(
                                                    with(intent) {
                                                        Msg.SelectedLocation(
                                                            "${location.name}, ${location.state}${location.zip}, ${location.country}",
                                                            isErrorLoadingForecasts = true
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                }
                            }
                        }

                    }
                    refresherListener?.let {
                        refresher.addListener(it)
                    }
                    if(refresher.isRefresherStarted()) {
                        refresher.forceRefresh(intent.location)
                    } else {
                        refresher.startBackgroundRefresher(intent.location)
                    }
                }
            }

        fun dispatchForecast(location: Location, threeHourForecasts: List<Forecast>) {
            val dailyForecasts = threeHourForecasts
                .groupBy { it.getLocalDateTime().dayOfYear }
                .filter {
                    it.value.size == 8 ||
                            it.value.first()
                                .getLocalDateTime().dayOfYear == Clock.System.now()
                        .toLocalDateTime(TimeZone.currentSystemDefault()).dayOfYear
                }
                .map { mapEntry ->
                    val baseIndex = mapEntry.value.size / 2
                    val minTemperature = mapEntry.value.minBy { it.temperatureMin }.temperatureMin
                    val maxTemperature = mapEntry.value.maxBy { it.temperatureMax }.temperatureMin
                    mapEntry.value[baseIndex].copy(
                        temperatureMin = minTemperature,
                        temperatureMax = maxTemperature
                    )
                }
            dispatch(
                    Msg.SelectedLocation(
                        "${location.name}, ${location.state}${location.zip}, ${location.country}",
                        locationForecasts = dailyForecasts
                    )
            )
        }
    }

    private object ReducerImpl : Reducer<LocationSelectionStore.State, Msg> {
        override fun LocationSelectionStore.State.reduce(msg: Msg): LocationSelectionStore.State =
            when (msg) {
                is Msg.ListLoading -> LocationSelectionStore.State.Loading(
                    msg.query,
                    msg.query.isNotEmpty()
                )

                is Msg.Error -> LocationSelectionStore.State.Error(
                    msg.query,
                    msg.errorMessage,
                    msg.query.isNotEmpty()
                )

                is Msg.ListLoaded -> LocationSelectionStore.State.FoundLocations(
                    msg.searchQuery,
                    msg.locations,
                    true
                )

                is Msg.SelectedLocation ->
                    LocationSelectionStore.State.SelectedLocation(
                        msg.locationTitle,
                        isLoadingForecasts = msg.isLoadingForecasts,
                        isErrorLoadingForecasts = msg.isErrorLoadingForecasts,
                        locationForecasts = msg.locationForecasts,
                    )
            }
    }

}