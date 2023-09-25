import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.weatherapp.core.model.Location
import com.weatherapp.ui.locationselection.LocationSelectionComponent
import com.weatherapp.ui.locationselection.LocationSelectionScreen
import com.weatherapp.ui.settings.SettingsComponent
import kotlinx.datetime.LocalDateTime

actual fun getPlatformName(): String = "Desktop"

@Composable
fun MainView(
    settingsComponent: SettingsComponent,
    locationComponent: LocationSelectionComponent,
    onForecastForLocationClicked: (Location, LocalDateTime) -> Unit,
) = LocationSelectionScreen(settingsComponent, locationComponent, onForecastForLocationClicked)

@Preview
@Composable
fun AppPreview() {
    App()
}