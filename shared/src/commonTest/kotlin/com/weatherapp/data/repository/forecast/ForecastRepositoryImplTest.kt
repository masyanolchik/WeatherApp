package com.weatherapp.data.repository.forecast

import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.createDatabase
import com.weatherapp.core.database.dao.ForecastDao
import com.weatherapp.core.database.dao.LocationDao
import com.weatherapp.core.database.dao.testSqlDriverFactory
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.core.network.client.WeatherApiClient
import com.weatherapp.core.network.client.WeatherApiClientTest.Companion.FORECAST_SUCCESS
import com.weatherapp.core.network.client.WeatherApiClientTest.Companion.LOCATION_ENTITY
import com.weatherapp.core.network.client.WeatherApiClientTest.Companion.getHttpClient
import com.weatherapp.core.network.error.WeatherApiException
import com.weatherapp.data.repository.location.LocationRepositoryImpl
import com.weatherapp.data.repository.location.LocationRepositoryImplTest
import com.weatherapp.data.toForecastEntity
import com.weatherapp.data.toLocationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.File
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ForecastRepositoryImplTest: KoinTest {
    private val forecastRepositoryImpl: ForecastRepositoryImpl by inject()
    private val locationDao: LocationDao by inject()
    private val forecastDao: ForecastDao by inject()

    fun before(forecastResponse: String) {
        deleteDataBase()

        startKoin {
            modules(
                listOf(
                    module {
                        single{ WeatherApiClient(getHttpClient(forecastResponse)) }
                    },
                    module {
                        factory { testSqlDriverFactory() }
                        single { createDatabase(driver = get()) }
                        single { LocationDao(get()) }
                        single { ForecastDao(get()) }
                    },
                    module {
                        single { ForecastRepositoryImpl(get(),get(), get()) }
                    }
                )
            )
        }
    }

    fun after() {
        deleteDataBase()
        stopKoin()
    }

    @Test
    fun testForecastRepositoryImpl_getForecastsForLocation() = runTest {
        before(FORECAST_SUCCESS)

        locationDao.insert(LOCATION_ENTITY.toLocationEntity(false))
        val forecasts = forecastRepositoryImpl.getForecastsForLocation(LOCATION_ENTITY, WeatherUnit.METRIC).first()

        assertTrue(forecasts.isSuccess)

        after()
    }

    @Test
    fun testForecastRepositoryImpl_getForecastsForLocation_networkError() = runTest {
        before("")

        locationDao.insert(LOCATION_ENTITY.toLocationEntity(false))
        val forecasts = forecastRepositoryImpl.getForecastsForLocation(LOCATION_ENTITY, WeatherUnit.METRIC).first()

        assertTrue(forecasts.isFailure)
        assertTrue((forecasts.exceptionOrNull() is WeatherApiException))

        after()
    }

    @Test
    fun testForecastRepositoryImpl_getSavedForecastsForLocation() = runTest {
        before("")

        locationDao.insert(LOCATION_ENTITY.toLocationEntity(false))
        val locationEntity = locationDao.getAllLocations().first()
        forecastDao.insert(FORECAST_ENTITY.toForecastEntity(locationEntity))
        val forecasts = forecastRepositoryImpl.getSavedForecastsForLocation(LOCATION_ENTITY).first()

        assertTrue(forecasts.isSuccess)
        assertTrue(forecasts.getOrNull()?.first()?.equals(FORECAST_ENTITY) ?: false)

        after()
    }

    fun deleteDataBase() {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "${DatabaseConstants.testName}.db")
        databasePath.delete()
    }

    companion object {
        val FORECAST_ENTITY = Forecast(
            date = 0L,
            temperature = "",
            feelsLikeTemperature = "",
            humidityPercentage = "",
            weatherTitle = "",
            weatherDescription = "",
            weatherIconId = "",
            cloudinessPercentage = "",
            windSpeed = "",
            windDirectionDegrees = "",
            windGust = "",
            visibilityMeters = 10000,
            location = LOCATION_ENTITY
        )
    }
}