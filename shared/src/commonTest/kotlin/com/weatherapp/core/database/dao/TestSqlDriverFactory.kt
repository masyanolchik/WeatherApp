package com.weatherapp.core.database.dao

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.WeatherDatabase
import org.koin.core.scope.Scope
import java.io.File

expect fun Scope.testSqlDriverFactory(): SqlDriver