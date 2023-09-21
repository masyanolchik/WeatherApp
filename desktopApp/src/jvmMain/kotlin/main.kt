import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.arkivanov.essenty.lifecycle.resume
import com.arkivanov.mvikotlin.core.utils.setMainThreadId
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import com.weatherapp.core.di.initKoin
import com.weatherapp.ui.locationselection.LocationSelectionComponent
import com.weatherapp.ui.theme.WeatherAppTheme
import javax.swing.SwingUtilities

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

    application {
        Window(onCloseRequest = ::exitApplication) {
            WeatherAppTheme {
                MainView(locationSelectionComponent)
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