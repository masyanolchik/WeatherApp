package com.weatherapp.data.di

import com.weatherapp.data.repository.forecast.ForecastRepository
import com.weatherapp.data.repository.forecast.ForecastRepositoryImpl
import com.weatherapp.data.repository.location.LocationRepository
import com.weatherapp.data.repository.location.LocationRepositoryImpl
import com.weatherapp.data.repository.settings.SettingsRepository
import com.weatherapp.data.repository.settings.SettingsRepositoryImpl
import org.koin.dsl.binds
import org.koin.dsl.module

val dataModule = module {
    single { SettingsRepositoryImpl(get()) } binds arrayOf(SettingsRepository::class)
    single{ LocationRepositoryImpl(get(), get()) } binds arrayOf(LocationRepository::class)
    single{ ForecastRepositoryImpl(get(),get(),get()) } binds arrayOf(ForecastRepository::class)
}