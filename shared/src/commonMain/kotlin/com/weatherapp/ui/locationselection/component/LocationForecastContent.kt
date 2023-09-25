package com.weatherapp.ui.locationselection.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.ui.AsyncImage
import com.weatherapp.ui.forecast.component.getDegreesString
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.settings.store.SettingsStore
import com.weatherapp.ui.theme.ColorSurface
import kotlinx.datetime.LocalDateTime

@Composable
fun LocationForecastContent(
    locationSelectionState: LocationSelectionStore.State,
    settingsState: SettingsStore.State,
    modifier: Modifier = Modifier,
    onItemClicked: (Location, LocalDateTime) -> Unit
) {
    Surface(color = ColorSurface) {
        if(locationSelectionState is LocationSelectionStore.State.SelectedLocation) {
            when {
                locationSelectionState.isLoadingForecasts -> {
                    LocationForecastProgressBar(modifier.fillMaxSize())
                }
                locationSelectionState.isErrorLoadingForecasts -> {
                    LocationForecastError(modifier.fillMaxSize())
                }
                locationSelectionState.locationForecasts.isNotEmpty() -> {
                    LocationForecastList(
                        settingsState = settingsState,
                        list = locationSelectionState.locationForecasts,
                        onItemClicked = onItemClicked,
                        modifier = modifier.fillMaxSize()
                    )
                }
                else -> {
                    Text("Select location from the search")
                }
            }
        }
    }
}

@Composable
fun LocationForecastProgressBar(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
@Composable
fun LocationForecastError(
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Row(
            modifier = Modifier.align(Alignment.Center)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.padding(4.dp))
            Text("Error loading forecasts for the given location")
        }
    }
}

@Composable
fun LocationForecastList(
    settingsState: SettingsStore.State,
    list: List<Forecast> = emptyList(),
    onItemClicked: (Location, LocalDateTime) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val degreesString = getDegreesString(settingsState)
    LazyColumn(modifier) {
        items(list) {
            ListItem(
                leadingContent = {
                    AsyncImage(
                        url = it.weatherIconUrl,
                        contentDescription = "Icon ${it.weatherTitle}",
                        modifier = Modifier.size(56.dp)
                    )
                },
                headlineContent = {
                    Text(it.getReadableDayOfWeekRepresentation())
                },
                supportingContent = {
                    Text(it.weatherTitle)
                },
                trailingContent = {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("${it.temperature}$degreesString")
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "${it.temperatureMin}$degreesString",
                                fontWeight = FontWeight.Thin
                            )
                            Text(
                                text = "${it.temperatureMax}$degreesString",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                modifier = modifier.clickable {
                    onItemClicked(it.location, it.getLocalDateTime())
                }
            )
        }
    }
}