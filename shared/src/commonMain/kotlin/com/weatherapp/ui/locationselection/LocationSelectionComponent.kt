package com.weatherapp.ui.locationselection

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.bind
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.locationselection.store.LocationSelectionStoreFactory
import kotlinx.coroutines.flow.StateFlow

class LocationSelectionComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
    onLabel: (LocationSelectionStore.Label) -> Unit,
):  ComponentContext by componentContext {

    private val locationSelectionStore =
        instanceKeeper.getStore {
            LocationSelectionStoreFactory(
                storeFactory = storeFactory
            ).create()
        }

    val state: StateFlow<LocationSelectionStore.State> = locationSelectionStore.stateFlow

    init {
        bind {
            locationSelectionStore.labels bindTo { label ->
               onLabel(label)
            }
        }
    }

    fun onEvent(event: LocationSelectionStore.Intent) {
        locationSelectionStore.accept(event)
    }
}