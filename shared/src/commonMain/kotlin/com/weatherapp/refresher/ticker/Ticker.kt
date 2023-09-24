package com.weatherapp.refresher.ticker

import kotlinx.coroutines.flow.SharedFlow

interface Ticker {
    val tickerFlow: SharedFlow<Unit>
    fun start(delayMs: Long)

    fun stop()
}