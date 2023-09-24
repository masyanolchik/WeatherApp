package com.weatherapp.ui.refresher

import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location

interface ForecastRefresher {

    fun startBackgroundRefresher(location: Location)

    fun isRefresherStarted(): Boolean
    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun forceRefresh(location: Location)

    interface Listener {
        fun onRefreshStarted(location: Location)
        fun onRefreshFinished(result: Result<List<Forecast>>)
    }
}