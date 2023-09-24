package com.weatherapp.ui.weatherchangenotifier.di

import com.weatherapp.ui.weatherchangenotifier.WeatherChangeNotifier
import com.weatherapp.ui.weatherchangenotifier.WeatherChangeNotifierImplementation
import org.koin.dsl.binds
import org.koin.dsl.module

val weatherNotifierModule = module {
    single { WeatherChangeNotifierImplementation(get()) } binds arrayOf(WeatherChangeNotifier::class)
}