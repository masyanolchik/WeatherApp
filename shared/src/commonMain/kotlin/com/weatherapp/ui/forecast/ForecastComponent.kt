package com.weatherapp.ui.forecast

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.weatherapp.ui.forecast.store.ForecastStore
import com.weatherapp.ui.forecast.store.ForecastStoreFactory
import kotlinx.coroutines.flow.StateFlow

class ForecastComponent(
    componentContext: ComponentContext,
    storeFactory: StoreFactory,
):  ComponentContext by componentContext {

    private val forecastStore by lazy {
        instanceKeeper.getStore {
            ForecastStoreFactory(
                storeFactory = storeFactory
            ).create()
        }
    }

    val state: StateFlow<ForecastStore.State> by lazy {
        forecastStore.stateFlow
    }

    fun onEvent(event: ForecastStore.Intent) {
        forecastStore.accept(event)
    }
}