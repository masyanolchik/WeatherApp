package com.weatherapp.data.di

import com.weatherapp.data.repository.forecast.ForecastRepository
import com.weatherapp.data.repository.forecast.ForecastRepositoryImpl
import com.weatherapp.data.repository.location.LocationRepository
import com.weatherapp.data.repository.location.LocationRepositoryImpl
import org.koin.dsl.binds
import org.koin.dsl.module

val dataModule = module {
    single{ LocationRepositoryImpl(get(), get()) } binds arrayOf(LocationRepository::class)
    single{ ForecastRepositoryImpl(get(),get(),get()) } binds arrayOf(ForecastRepository::class)
}