package com.weatherapp.ui.locationselection.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Forecast
import com.weatherapp.ui.AsyncImage
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.theme.ColorSurface
import kotlin.reflect.KFunction1

@Composable
fun LocationForecastContent(
    state: LocationSelectionStore.State,
    modifier: Modifier = Modifier,
    onEvent:(LocationSelectionStore.Intent) -> Unit = { },
) {
    Surface(color = ColorSurface) {
        if(state is LocationSelectionStore.State.SelectedLocation) {
            when {
                state.isLoadingForecasts -> {
                    LocationForecastProgressBar(modifier.fillMaxSize())
                }
                state.isErrorLoadingForecasts -> {
                    LocationForecastError(modifier.fillMaxSize())
                }
                state.locationForecasts.isNotEmpty() -> {
                    LocationForecastList(
                        list = state.locationForecasts,
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
    list: List<Forecast> = emptyList(),
    onItemClicked: (Forecast) -> Unit = {},
    modifier: Modifier = Modifier
) {
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
                    Text("${it.temperature}C")
                },
                modifier = modifier.clickable {
                    onItemClicked(it)
                }
            )
        }
    }
}