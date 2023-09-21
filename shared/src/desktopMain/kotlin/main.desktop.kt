import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.weatherapp.ui.locationselection.LocationSelectionComponent
import com.weatherapp.ui.locationselection.LocationSelectionScreen

actual fun getPlatformName(): String = "Desktop"

@Composable fun MainView(component: LocationSelectionComponent) = LocationSelectionScreen(component)

@Preview
@Composable
fun AppPreview() {
    App()
}