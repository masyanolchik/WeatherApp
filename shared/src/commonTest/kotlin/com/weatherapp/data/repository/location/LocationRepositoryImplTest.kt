package com.weatherapp.data.repository.location

import com.weatherapp.core.database.DatabaseConstants
import com.weatherapp.core.database.createDatabase
import com.weatherapp.core.database.dao.LocationDao
import com.weatherapp.core.database.dao.testSqlDriverFactory
import com.weatherapp.core.network.client.WeatherApiClient
import com.weatherapp.core.network.client.WeatherApiClientTest.Companion.LOCATION_ENTITY
import com.weatherapp.core.network.client.WeatherApiClientTest.Companion.getHttpResponseData
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpStatusCode
import io.ktor.http.toURI
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.File
import java.io.IOException
import kotlin.test.assertTrue

class LocationRepositoryImplTest : KoinTest {
    private val locationRepositoryImpl: LocationRepositoryImpl by inject()

    // it is a dirty hack
    // we should be able to configure all of these with di
    // and configure tests with annotations
    // but i'm in a hurry, so i can't spend some time on it
    fun before(
        queryResponse: String = LOCATION_QUERY_SUCCESS,
        zipResponse: String = LOCATION_ZIP_SUCCESS
    ) {
        deleteDataBase()

        startKoin {
            modules(
                listOf(
                    module {
                       single{ WeatherApiClient(getHttpClient(
                           queryResponse = queryResponse,
                           zipResponse = zipResponse
                       )) }
                    },
                    module {
                        factory { testSqlDriverFactory() }
                        single { createDatabase(driver = get()) }
                        single { LocationDao(get()) }
                    },
                    module {
                        single { LocationRepositoryImpl(get(),get()) }
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
    fun testLocationRepository_getLocationQuery() = runTest {
        before()

        testScheduler.advanceUntilIdle()
        val location = locationRepositoryImpl.getLocations("").first()

        assertTrue(location.isSuccess)
        assertTrue(location.getOrNull()?.map { it.state }?.contains("Kharkiv Oblast") ?: false)

        after()
    }

    @Test
    fun testLocationRepository_getLocationZip() = runTest {
        before(
            queryResponse = "[]"
        )

        testScheduler.advanceUntilIdle()
        val location = locationRepositoryImpl.getLocations("").first()

        assertTrue(location.isSuccess)
        assertTrue(location.getOrNull()?.map { it.zip }?.contains("61000") ?: false)

        after()
    }

    @Test
    fun testLocationRepository_saveSelectedLocation() = runTest {
        before()

        testScheduler.advanceUntilIdle()
        val location = locationRepositoryImpl.getLocations("").first()
        locationRepositoryImpl.saveSelectedLocation(LOCATION_ENTITY)
        val selectedLocation = locationRepositoryImpl.getSavedSelectedLocation().first()
        assertTrue(location.isSuccess)
        assertTrue(selectedLocation.isSuccess)
        assertTrue(selectedLocation.getOrNull()?.equals(LOCATION_ENTITY) ?: false)

        after()
    }

    fun deleteDataBase() {
        val databasePath = File(System.getProperty("java.io.tmpdir"), "${DatabaseConstants.testName}.db")
        databasePath.delete()
    }

    companion object {
        fun getHttpClient(
            statusCode: HttpStatusCode = HttpStatusCode.OK,
            shouldThrowException: Boolean = false,
            queryResponse: String,
            zipResponse: String
        ): HttpClient {
            val mockEngine =
                getMockEngine(
                    statusCode,
                    shouldThrowException,
                    queryResponse,
                    zipResponse
                )
            return HttpClient(mockEngine) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
            }
        }
        fun getMockEngine(
            statusCode: HttpStatusCode,
            shouldThrowException: Boolean,
            queryResponse: String,
            zipResponse: String
        ): MockEngine {
            return MockEngine { request ->
                if (shouldThrowException) {
                    throw IOException("Something goes wrong during request")
                } else {
                    if(request.url.toURI().toString().contains("direct")) {
                        getHttpResponseData(queryResponse, statusCode)
                    } else {
                        getHttpResponseData(zipResponse, statusCode)
                    }
                }
            }
        }

        const val LOCATION_QUERY_SUCCESS =
            "[\n" +
                    "    {\n" +
                    "        \"name\": \"Kharkiv\",\n" +
                    "        \"local_names\": {\n" +
                    "            \"ku\": \"Xarkîv\",\n" +
                    "            \"io\": \"Kharkiv\",\n" +
                    "            \"sl\": \"Harkov\",\n" +
                    "            \"hr\": \"Harkiv\",\n" +
                    "            \"hu\": \"Harkiv\",\n" +
                    "            \"be\": \"Харкаў\",\n" +
                    "            \"he\": \"חרקוב\",\n" +
                    "            \"de\": \"Charkiw\",\n" +
                    "            \"cs\": \"Charkov\",\n" +
                    "            \"tt\": \"Харкау\",\n" +
                    "            \"ja\": \"ハルキウ\",\n" +
                    "            \"pl\": \"Charków\",\n" +
                    "            \"fi\": \"Harkova\",\n" +
                    "            \"ar\": \"خاركيف\",\n" +
                    "            \"lt\": \"Charkovas\",\n" +
                    "            \"tr\": \"Harkiv\",\n" +
                    "            \"es\": \"Járkiv\",\n" +
                    "            \"la\": \"Charcovia\",\n" +
                    "            \"el\": \"Χάρκοβο\",\n" +
                    "            \"ca\": \"Khàrkiv\",\n" +
                    "            \"zh\": \"哈尔科夫\",\n" +
                    "            \"en\": \"Kharkiv\",\n" +
                    "            \"kn\": \"ಖಾರ್ಕಿವ\",\n" +
                    "            \"fr\": \"Kharkiv\",\n" +
                    "            \"it\": \"Charkiv\",\n" +
                    "            \"ro\": \"Harkov\",\n" +
                    "            \"uk\": \"Харків\",\n" +
                    "            \"ko\": \"하르키우\",\n" +
                    "            \"eu\": \"Kharkiv\",\n" +
                    "            \"hi\": \"ख़ारकिव\",\n" +
                    "            \"nl\": \"Charkov\",\n" +
                    "            \"pt\": \"Carcóvia\",\n" +
                    "            \"eo\": \"Ĥarkivo\",\n" +
                    "            \"lv\": \"Harkiva\",\n" +
                    "            \"sv\": \"Charkiv\",\n" +
                    "            \"et\": \"Harkiv\",\n" +
                    "            \"ml\": \"ഖാർകിവ്\",\n" +
                    "            \"ru\": \"Харьков\",\n" +
                    "            \"oc\": \"Kharkiv\",\n" +
                    "            \"sr\": \"Харков\",\n" +
                    "            \"sk\": \"Charkov\"\n" +
                    "        },\n" +
                    "        \"lat\": 49.9923181,\n" +
                    "        \"lon\": 36.2310146,\n" +
                    "        \"country\": \"UA\",\n" +
                    "        \"state\": \"Kharkiv Oblast\"\n" +
                    "    }\n" +
                    "]"

        const val LOCATION_ZIP_SUCCESS =
            "{\n" +
                    "    \"zip\": \"61000\",\n" +
                    "    \"name\": \"Kharkiv\",\n" +
                    "    \"lat\": 49.9808,\n" +
                    "    \"lon\": 36.2527,\n" +
                    "    \"country\": \"UA\"\n" +
                    "}"
    }
}