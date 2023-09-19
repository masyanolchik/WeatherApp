package com.weatherapp.core.database.dao

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.WeatherDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.scope.Scope

actual fun Scope.testSqlDriverFactory(): SqlDriver {
    return AndroidSqliteDriver(WeatherDatabase.Schema, androidContext(), "${DatabaseConstants.testName}.db")
}