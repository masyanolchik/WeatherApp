package com.weatherapp.core.di

import com.weatherapp.core.database.di.databaseModule
import com.weatherapp.core.network.di.networkModule
import com.weatherapp.data.di.dataModule
import com.weatherapp.ui.refresher.di.refresherModule
import com.weatherapp.ui.weatherchangenotifier.di.weatherNotifierModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            databaseModule,
            networkModule(enableNetworkLogs),
            refresherModule,
            weatherNotifierModule,
            dataModule
        )
    }