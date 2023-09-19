package com.weatherapp.core.database.dao

import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.createDatabase
import comweatherapp.LocationEntity
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject
import org.koin.test.KoinTest
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LocationDaoTest : KoinTest {
    private val locationDao: LocationDao by inject(LocationDao::class.java)

    // before and after for some reason doesn't work
    fun before() {
        deleteDataBase()

        startKoin {
            modules(
                module {
                    factory { testSqlDriverFactory() }
                    single { createDatabase(driver = get()) }
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
    fun testLocationDao_insert() = runTest{
        before()

        this.testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)

        val insertedLocationEntities = locationDao.getAllLocations()
        assertTrue(insertedLocationEntities.isNotEmpty())
        assertTrue(insertedLocationEntities.size == 1)
        assertTrue(insertedLocationEntities.contains(SAMPLE_LOCATION_ENTITY))

        after()
    }

    @Test
    fun testLocationDao_delete() = runTest{
        before()

        this.testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)
        locationDao.delete(1)

        val insertedLocationEntities = locationDao.getAllLocations()
        assertTrue(insertedLocationEntities.isEmpty())

        after()
    }

    @Test
    fun testLocationDao_getByLatLon() = runTest{
        before()

        this.testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)

        val foundLocationEntity = locationDao.getLocationByLatitudeLongitude(
            SAMPLE_LOCATION_ENTITY.latitude,
            SAMPLE_LOCATION_ENTITY.longtitude
        )
        assertEquals(foundLocationEntity, SAMPLE_LOCATION_ENTITY)

        after()
    }

    @Test
    fun testLocationDao_clearTable() = runTest{
        before()

        testScheduler.advanceUntilIdle()
        locationDao.insert(SAMPLE_LOCATION_ENTITY)
        locationDao.insert(SAMPLE_LOCATION_ENTITY)
        locationDao.insert(SAMPLE_LOCATION_ENTITY)

        val foundLocationEntitiesBeforeClearing = locationDao.getAllLocations()
        locationDao.clearTable()
        val foundLocationEntitiesAfterClearing = locationDao.getAllLocations()

        assertTrue(foundLocationEntitiesBeforeClearing.size == 3)
        assertTrue(foundLocationEntitiesAfterClearing.isEmpty())

        after()
    }

    companion object {
        val SAMPLE_LOCATION_ENTITY = LocationEntity(
            id = 1,
            name = "LocName",
            latitude = "lat",
            longtitude = "lon",
            country = "US",
            state = "CA",
            zip = ""
        )
    }
}