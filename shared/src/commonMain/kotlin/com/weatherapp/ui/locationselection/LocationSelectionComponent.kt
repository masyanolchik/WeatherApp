package com.weatherapp.ui.locationselection

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.locationselection.store.LocationSelectionStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.replay
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import weatherAppDispatchers

class LocationSelectionComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
):  ComponentContext by componentContext {

    private val locationSelectionStore by lazy {
        instanceKeeper.getStore {
            LocationSelectionStoreFactory(
                storeFactory = storeFactory
            ).create()
        }

    }

    val state: StateFlow<LocationSelectionStore.State> by lazy {
        locationSelectionStore.stateFlow
    }

    fun getIncomingLabelFlow(coroutineScope: CoroutineScope) =
        locationSelectionStore
            .labels
            .flowOn(weatherAppDispatchers.main)
            .onEach {
                val k = 0
            }
            .stateIn(coroutineScope, SharingStarted.Eagerly, LocationSelectionStore.Label.NoLocationChosen)


    fun onEvent(event: LocationSelectionStore.Intent) {
        locationSelectionStore.accept(event)
    }
}