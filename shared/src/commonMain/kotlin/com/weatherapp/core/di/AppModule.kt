package com.weatherapp.core.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.weatherapp.core.database.di.databaseModule
import com.weatherapp.core.network.di.networkModule
import com.weatherapp.data.di.dataModule
import com.weatherapp.ui.refresher.di.refresherModule
import com.weatherapp.ui.settings.di.settingsModule
import com.weatherapp.ui.weatherchangenotifier.di.weatherNotifierModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(componentContext: ComponentContext, storeFactory: StoreFactory, enableNetworkLogs: Boolean = false, appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            networkModule(enableNetworkLogs),
            databaseModule,
            dataModule,
            settingsModule(componentContext, storeFactory),
            refresherModule,
            weatherNotifierModule,
        )
    }