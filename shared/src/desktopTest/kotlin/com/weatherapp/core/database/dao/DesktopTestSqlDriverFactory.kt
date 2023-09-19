package com.weatherapp.core.database.dao

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.WeatherDatabase
import org.koin.core.scope.Scope
import java.io.File

actual fun Scope.testSqlDriverFactory(): SqlDriver {
    val databasePath = File(System.getProperty("java.io.tmpdir"), "${DatabaseConstants.testName}.db")
    val driver = JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.path}")
    WeatherDatabase.Schema.create(driver)
    return driver
}