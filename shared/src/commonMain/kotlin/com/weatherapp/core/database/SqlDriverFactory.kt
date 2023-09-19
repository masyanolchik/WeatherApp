package com.weatherapp.core.database

import app.cash.sqldelight.db.SqlDriver
import org.koin.core.scope.Scope

expect fun Scope.sqlDriverFactory(): SqlDriver

fun createDatabase(driver: SqlDriver): WeatherDatabase {
    return WeatherDatabase(
        driver = driver,
    )
}