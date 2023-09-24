package com.weatherapp.refresher.ticker

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

// It would be better to use WorkManager in android
class TickerImplementation(coroutineDispatcher: CoroutineDispatcher): Ticker {
    private val coroutineScope = CoroutineScope(coroutineDispatcher)
    private var tickerJob: Job? = null

    private val _tickerFlow = MutableSharedFlow<Unit>(replay = 1)
    override val tickerFlow = _tickerFlow as SharedFlow<Unit>

    override fun start(delayMs: Long) {
        tickerJob = coroutineScope.launch {
            while(true) {
                try {
                    _tickerFlow.emit(Unit)
                    delay(delayMs)
                } finally {
                    // log ticker cancellation here
                }
            }
        }
    }

    override fun stop() {
        tickerJob?.cancel()
    }
}