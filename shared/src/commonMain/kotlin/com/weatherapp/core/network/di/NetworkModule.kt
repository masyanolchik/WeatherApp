package com.weatherapp.core.network.di

import com.weatherapp.core.network.client.WeatherApiClient
import com.weatherapp.core.network.createHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module

val networkModule: (enableLogging: Boolean) -> Module
    get() = { enableLogging ->
    module {
        single { createHttpClient(enableLogging) }
        single { WeatherApiClient(httpClient = get()) }
    }
}