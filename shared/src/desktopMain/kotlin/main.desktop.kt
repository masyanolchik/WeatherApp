import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.weatherapp.core.model.Location
import com.weatherapp.ui.locationselection.LocationSelectionComponent
import com.weatherapp.ui.locationselection.LocationSelectionScreen
import kotlinx.datetime.LocalDateTime

actual fun getPlatformName(): String = "Desktop"

@Composable
fun MainView(
    component: LocationSelectionComponent,
    onForecastForLocationClicked: (Location, LocalDateTime) -> Unit,
) = LocationSelectionScreen(component, onForecastForLocationClicked)

@Preview
@Composable
fun AppPreview() {
    App()
}