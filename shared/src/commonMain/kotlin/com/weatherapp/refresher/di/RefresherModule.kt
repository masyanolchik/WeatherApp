package com.weatherapp.refresher.di

import com.weatherapp.refresher.ForecastRefresher
import com.weatherapp.refresher.ForecastRefresherImplementation
import com.weatherapp.refresher.ticker.Ticker
import com.weatherapp.refresher.ticker.TickerImplementation
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