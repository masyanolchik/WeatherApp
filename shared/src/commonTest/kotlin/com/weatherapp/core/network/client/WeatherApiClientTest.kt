package com.weatherapp.core.network.client

import com.weatherapp.core.model.Location
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.core.network.error.WeatherApiException
import com.weatherapp.core.network.error.WeatherError
import com.weatherapp.core.network.model.LocationQueryApiResponse
import com.weatherapp.core.network.model.LocationZipApiResponse
import io.kotest.core.spec.style.AnnotationSpec
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertTrue

class WeatherApiClientTest {

    @Test
    fun testGetLocationByQuery_success() = runTest {
        val httpClient = getHttpClient(LOCATION_QUERY_SUCCESS)
        val apiClient = WeatherApiClient(httpClient)

        val locations = apiClient.getLocationByQuery("").first()

        assertTrue(locations.isNotEmpty())
    }

    @Test
    fun testGetLocationByQuery_clientError() = runTest {
        val httpClient = getHttpClient(RESPONSE_CLIENT_ERROR, HttpStatusCode.NotFound)
        val apiClient = WeatherApiClient(httpClient)

        try {
            apiClient.getLocationByQuery("").first()
        } catch (ex: Exception) {
            assertTrue(ex is WeatherApiException)
            assertTrue(
                ex.message?.equals(WeatherApiException(WeatherError.ClientError).message) ?: false
            )
        }
    }

    @Test
    fun testGetLocationByQuery_serverError() = runTest {
        val httpClient = getHttpClient(statusCode = HttpStatusCode.InternalServerError)
        val apiClient = WeatherApiClient(httpClient)

        try {
            apiClient.getLocationByQuery("").first()
        } catch (ex: Exception) {
            assertTrue(ex is WeatherApiException)
            assertTrue(
                ex.message?.equals(WeatherApiException(WeatherError.ServerError).message) ?: false
            )
        }
    }

    @Test
    fun testGetLocationByQuery_unknownError() = runTest {
        val httpClient = getHttpClient(statusCode = HttpStatusCode.BadGateway)
        val apiClient = WeatherApiClient(httpClient)

        try {
            apiClient.getLocationByQuery("").first()
        } catch (ex: Exception) {
            assertTrue(ex is WeatherApiException)
            assertTrue(
                ex.message?.equals(WeatherApiException(WeatherError.UnknownError).message) ?: false
            )
        }
    }

    @Test
    fun testGetLocationByQuery_serviceUnavailableError() = runTest {
        val httpClient = getHttpClient(shouldThrowException = true)
        val apiClient = WeatherApiClient(httpClient)

        try {
            apiClient.getLocationByQuery("").first()
        } catch (ex: Exception) {
            assertTrue(ex is WeatherApiException)
            assertTrue(
                ex.message?.equals(WeatherApiException(WeatherError.ServiceUnavailable).message)
                    ?: false
            )
        }
    }

    @Test
    fun testGetLocationByQuery_resultRetrievingError() = runTest {
        val httpClient = getHttpClient("FAULTY RESPONSE")
        val apiClient = WeatherApiClient(httpClient)

        try {
            apiClient.getLocationByQuery("").first()
        } catch (ex: Exception) {
            assertTrue(ex is WeatherApiException)
            assertTrue(
                ex.message?.equals(WeatherApiException(WeatherError.ResultRetrievingError).message)
                    ?: false
            )
        }
    }

    @Test
    fun testGetLocationByZip_resultSuccess() = runTest {
        val httpClient = getHttpClient(LOCATION_ZIP_SUCCESS)
        val apiClient = WeatherApiClient(httpClient)

        val location = apiClient.getLocationByZipCode("").first()

        assertTrue(location.country == "UA")
    }

    @Test
    fun testGetWeatherForecast_resultSuccess() = runTest {
        val httpClient = getHttpClient(FORECAST_SUCCESS)
        val apiClient = WeatherApiClient(httpClient)

        val forecast = apiClient.getWeatherForecast(LOCATION_ENTITY, WeatherUnit.METRIC).first()

        assertTrue(forecast.city.name == "Kharkiv")

    }

    private fun getHttpClient(
        response: String = "",
        statusCode: HttpStatusCode = HttpStatusCode.OK,
        shouldThrowException: Boolean = false
    ): HttpClient {
        val mockEngine = getMockEngine(response, statusCode, shouldThrowException)
        return HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
    }

    private fun getMockEngine(
        response: String,
        statusCode: HttpStatusCode,
        shouldThrowException: Boolean,
    ): MockEngine {
        return MockEngine { _ ->
            if (shouldThrowException) {
                throw IOException("Something goes wrong during request")
            } else {
                getHttpResponseData(response, statusCode)
            }
        }
    }

    private fun MockRequestHandleScope.getHttpResponseData(
        response: String,
        statusCode: HttpStatusCode
    ): HttpResponseData {
        return respond(
            content = ByteReadChannel(response),
            status = statusCode,
            headers = headersOf(HttpHeaders.ContentType, "application/json")
        )
    }

    companion object {
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

        const val RESPONSE_CLIENT_ERROR =
            "{\n" +
                    "    \"cod\": \"404\",\n" +
                    "    \"message\": \"not found\"\n" +
                    "}"

        const val FORECAST_SUCCESS =
            "{\n" +
                    "    \"cod\": \"200\",\n" +
                    "    \"message\": 0,\n" +
                    "    \"cnt\": 40,\n" +
                    "    \"list\": [" +
                    "{\n" +
                    "            \"dt\": 1695049200,\n" +
                    "            \"main\": {\n" +
                    "                \"temp\": 24.28,\n" +
                    "                \"feels_like\": 23.55,\n" +
                    "                \"temp_min\": 22.99,\n" +
                    "                \"temp_max\": 24.28,\n" +
                    "                \"pressure\": 1024,\n" +
                    "                \"sea_level\": 1024,\n" +
                    "                \"grnd_level\": 1010,\n" +
                    "                \"humidity\": 30,\n" +
                    "                \"temp_kf\": 1.29\n" +
                    "            },\n" +
                    "            \"weather\": [\n" +
                    "                {\n" +
                    "                    \"id\": 800,\n" +
                    "                    \"main\": \"Clear\",\n" +
                    "                    \"description\": \"clear sky\",\n" +
                    "                    \"icon\": \"01d\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"clouds\": {\n" +
                    "                \"all\": 6\n" +
                    "            },\n" +
                    "            \"wind\": {\n" +
                    "                \"speed\": 2.62,\n" +
                    "                \"deg\": 298,\n" +
                    "                \"gust\": 3.24\n" +
                    "            },\n" +
                    "            \"visibility\": 10000,\n" +
                    "            \"pop\": 0,\n" +
                    "            \"sys\": {\n" +
                    "                \"pod\": \"d\"\n" +
                    "            },\n" +
                    "            \"dt_txt\": \"2023-09-18 15:00:00\"\n" +
                    "        }," +
                    "{\n" +
                    "            \"dt\": 1695060000,\n" +
                    "            \"main\": {\n" +
                    "                \"temp\": 21.43,\n" +
                    "                \"feels_like\": 20.59,\n" +
                    "                \"temp_min\": 19.68,\n" +
                    "                \"temp_max\": 21.43,\n" +
                    "                \"pressure\": 1023,\n" +
                    "                \"sea_level\": 1023,\n" +
                    "                \"grnd_level\": 1010,\n" +
                    "                \"humidity\": 37,\n" +
                    "                \"temp_kf\": 1.75\n" +
                    "            },\n" +
                    "            \"weather\": [\n" +
                    "                {\n" +
                    "                    \"id\": 801,\n" +
                    "                    \"main\": \"Clouds\",\n" +
                    "                    \"description\": \"few clouds\",\n" +
                    "                    \"icon\": \"02n\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"clouds\": {\n" +
                    "                \"all\": 12\n" +
                    "            },\n" +
                    "            \"wind\": {\n" +
                    "                \"speed\": 1.6,\n" +
                    "                \"deg\": 291,\n" +
                    "                \"gust\": 1.73\n" +
                    "            },\n" +
                    "            \"visibility\": 10000,\n" +
                    "            \"pop\": 0,\n" +
                    "            \"sys\": {\n" +
                    "                \"pod\": \"n\"\n" +
                    "            },\n" +
                    "            \"dt_txt\": \"2023-09-18 18:00:00\"\n" +
                    "        }" +
                    "],\n" +
                    "    \"city\": {\n" +
                    "        \"id\": 706483,\n" +
                    "        \"name\": \"Kharkiv\",\n" +
                    "        \"coord\": {\n" +
                    "            \"lat\": 49.9791,\n" +
                    "            \"lon\": 36.246\n" +
                    "        },\n" +
                    "        \"country\": \"UA\",\n" +
                    "        \"population\": 1430885,\n" +
                    "        \"timezone\": 10800,\n" +
                    "        \"sunrise\": 1695006876,\n" +
                    "        \"sunset\": 1695051858\n" +
                    "    }\n" +
                    "}"

        val LOCATION_ENTITY = Location(
            name = "Kharkiv",
            latitude = "49.9923181",
            longitude = "36.2310146",
            country = "UA",
            state = "Kharkivska"
        )
    }

}