package com.weatherapp.ui.settings.di

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.weatherapp.ui.settings.SettingsComponent
import org.koin.dsl.module

fun settingsModule(componentContext: ComponentContext, storeFactory: StoreFactory) = module {
    single { SettingsComponent(componentContext, storeFactory) }
}