package com.weatherapp.core.database.di

import com.weatherapp.core.database.createDatabase
import com.weatherapp.core.database.dao.ForecastDao
import com.weatherapp.core.database.dao.LocationDao
import com.weatherapp.core.database.sqlDriverFactory
import org.koin.dsl.module

val databaseModule = module {
    factory { sqlDriverFactory() }
    single { createDatabase(driver = get()) }
    single { LocationDao(weatherDatabase = get()) }
    single { ForecastDao(weatherDatabase = get()) }
}