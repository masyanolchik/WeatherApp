package com.weatherapp.ui.refresher

import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.data.repository.forecast.ForecastRepository
import com.weatherapp.ui.refresher.ticker.Ticker
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

class ForecastRefresherImplementation(
    private val coroutineDispatcher: CoroutineDispatcher,
    private val forecastRepository: ForecastRepository,
    private val ticker: Ticker
): ForecastRefresher {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)
    private val listeners = mutableListOf<ForecastRefresher.Listener>()
    private val locationState = MutableStateFlow<Location?>(null)
    private val forecastRefreshFlow = ticker.tickerFlow
            .flatMapLatest {
                locationState.filterNotNull()
            }.flatMapLatest { location ->
                listeners.forEach { it.onRefreshStarted(location) }
                forecastRepository.getForecastsForLocation(location, WeatherUnit.METRIC)
            }.onEach { forecasts ->
                listeners.forEach { it.onRefreshFinished(forecasts) }
            }.shareIn(coroutineScope, SharingStarted.Eagerly)
    private var refresherStarted = false

    override fun startBackgroundRefresher(location: Location) {
        locationState.value = location
        ticker.start(TICKER_DELAY)
        forecastRefreshFlow.launchIn(coroutineScope)
        refresherStarted = true
    }

    override fun isRefresherStarted() = refresherStarted

    override fun addListener(listener: ForecastRefresher.Listener) {
        listeners.add(listener)
    }

    override fun removeListener(listener: ForecastRefresher.Listener) {
        listeners.remove(listener)
    }


    override fun forceRefresh(location: Location) {
        ticker.stop()
        locationState.value = location
        ticker.start(TICKER_DELAY)
    }

    companion object {
        private const val TICKER_DELAY = 10_000L
    }
}