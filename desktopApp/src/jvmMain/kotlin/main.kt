import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.weatherapp.core.di.initKoin
import com.weatherapp.core.model.Location
import com.weatherapp.ui.forecast.ForecastComponent
import com.weatherapp.ui.forecast.ForecastScreen
import com.weatherapp.ui.forecast.store.ForecastStore
import com.weatherapp.ui.locationselection.LocationSelectionComponent
import com.weatherapp.ui.theme.WeatherAppTheme
import javax.swing.SwingUtilities
import kotlinx.datetime.LocalDateTime

fun main() {
    initKoin(enableNetworkLogs = false)

    val locationSelectionComponent = invokeOnAwtSync {
        setMainThreadId(Thread.currentThread().id)

        val lifecycle = LifecycleRegistry()

        val locationComponent = LocationSelectionComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            storeFactory = DefaultStoreFactory(),
        )

        lifecycle.resume()

        locationComponent
    }

    val forecastComponent = invokeOnAwtSync {
        val lifecycle = LifecycleRegistry()

        val frcComponent = ForecastComponent(
            componentContext = DefaultComponentContext(lifecycle = lifecycle),
            storeFactory = DefaultStoreFactory(),
        )

        lifecycle.resume()

        frcComponent
    }

    application {

        var forecastOpened by rememberSaveable { mutableStateOf(false) }
        val openForecastWindow: (Location, LocalDateTime) -> Unit = { location, localDateTime ->
            forecastOpened = true
            forecastComponent.onEvent(ForecastStore.Intent.ShowForecastForLocationForDay(location, localDateTime))
        }
        WeatherAppTheme {
            Window(
                title = "Forecast for a day",
                visible =  forecastOpened,
                onCloseRequest = {
                   forecastOpened = false
                },
                state = WindowState(
                    position = WindowPosition.Aligned(Alignment.Center)
                )
            ) {
                ForecastScreen(
                    forecastComponent = forecastComponent,
                    onCloseClick = {
                        forecastOpened = false
                    }
                )
            }
            Window(
                title = "Weather display app",
                onCloseRequest = ::exitApplication,
            ) {
                MainView(
                    locationSelectionComponent,
                    openForecastWindow
                )
            }
        }
    }
}

fun <T> invokeOnAwtSync(block: () -> T): T {
    var result: T? = null
    SwingUtilities.invokeAndWait { result = block() }

    @Suppress("UNCHECKED_CAST")
    return result as T
}