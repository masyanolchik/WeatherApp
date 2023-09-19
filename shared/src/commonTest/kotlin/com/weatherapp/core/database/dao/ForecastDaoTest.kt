package com.weatherapp.core.database.dao

import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.createDatabase
import com.weatherapp.core.database.dao.LocationDaoTest.Companion.SAMPLE_LOCATION_ENTITY
import comweatherapp.ForecastEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ForecastDaoTest {
    private val forecastDao: ForecastDao by inject(ForecastDao::class.java)
    private val locationDao: LocationDao by inject(LocationDao::class.java)

    fun before() {
        deleteDataBase()

        startKoin {
            modules(
                module {
                    factory { testSqlDriverFactory() }
                    single { createDatabase(driver = get()) }
                    single { ForecastDao(get()) }
                    single { LocationDao(get()) }
                }
            )
        }
    }


    fun after() {
        deleteDataBase()
        stopKoin()
    }

    fun deleteDataBase() {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "${DatabaseConstants.testName}.db")
        databasePath.delete()
    }

    @Test
    fun testForecastDao_insert() = runTest {
        before()

        testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)
        forecastDao.insert(SAMPLE_FORECAST_ENTITY)

        val foundForecasts = forecastDao.getAllForecasts()
        assertTrue(foundForecasts.isNotEmpty())
        assertEquals(foundForecasts.first().first, SAMPLE_FORECAST_ENTITY)
        assertEquals(foundForecasts.first().second, SAMPLE_LOCATION_ENTITY)

        after()
    }

    @Test
    fun testForecastDao_delete() = runTest {
        before()

        testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)
        forecastDao.insert(SAMPLE_FORECAST_ENTITY)

        val foundForecastsBeforeDeletingForecast = forecastDao.getAllForecasts()
        forecastDao.delete(1L)
        val foundForecastsAfterDeletingForecast = forecastDao.getAllForecasts()

        assertTrue(foundForecastsBeforeDeletingForecast.isNotEmpty())
        assertTrue(foundForecastsAfterDeletingForecast.isEmpty())

        after()
    }

    @Test
    fun testForecastDao_clearTable() = runTest {
        before()

        testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)
        forecastDao.insert(SAMPLE_FORECAST_ENTITY)

        val foundForecastsBeforeDeletingForecast = forecastDao.getAllForecasts()
        forecastDao.clearTable()
        val foundForecastsAfterDeletingForecast = forecastDao.getAllForecasts()

        assertTrue(foundForecastsBeforeDeletingForecast.isNotEmpty())
        assertTrue(foundForecastsAfterDeletingForecast.isEmpty())

        after()
    }

    companion object {
        val SAMPLE_FORECAST_ENTITY = ForecastEntity(
            id = 1,
            date = 1695049200L,
            temperature = "24.28",
            feelsLikeTemperature = "23.55",
            humidityPercentage = "30",
            weatherTitle = "Clear",
            weatherDescription = "clear sky",
            weatherIconId = "01d",
            cloudinessPercentage = "6",
            windSpeed = "2.62",
            windDirectionDegrees = "298",
            windGust = "3.24",
            visibilityMeters = 10000L,
            locationId = 1
        )
    }
}