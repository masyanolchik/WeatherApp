package com.weatherapp.ui.refresher.di

import com.weatherapp.ui.refresher.ForecastRefresher
import com.weatherapp.ui.refresher.ForecastRefresherImplementation
import com.weatherapp.ui.refresher.ticker.Ticker
import com.weatherapp.ui.refresher.ticker.TickerImplementation
import org.koin.dsl.binds
import org.koin.dsl.module
import weatherAppDispatchers

val refresherModule = module {
    single { TickerImplementation(weatherAppDispatchers.io) } binds arrayOf(Ticker::class)
    single {
        ForecastRefresherImplementation(
            weatherAppDispatchers.main,
            get(),
            get()
        )
    } binds arrayOf(ForecastRefresher::class)
}