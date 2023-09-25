package com.weatherapp.ui.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.settings.store.SettingsStore
import com.weatherapp.ui.settings.store.SettingsStoreFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import weatherAppDispatchers

class SettingsComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
): ComponentContext by componentContext  {

    private val settingsStore by lazy {
        instanceKeeper.getStore {
            SettingsStoreFactory(
                storeFactory = storeFactory
            ).create()
        }
    }

    val state: StateFlow<SettingsStore.State> by lazy {
        settingsStore.stateFlow
    }

    fun getIncomingLabelFlow(coroutineScope: CoroutineScope) =
        settingsStore
            .labels
            .flowOn(weatherAppDispatchers.main)
            .buffer(2)
            .stateIn(coroutineScope, SharingStarted.Eagerly, SettingsStore.Label.NoUpdatesLabel)



    fun onEvent(event: SettingsStore.Intent) {
        settingsStore.accept(event)
    }
}