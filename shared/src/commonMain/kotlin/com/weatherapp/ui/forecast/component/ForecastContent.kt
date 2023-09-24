package com.weatherapp.ui.forecast.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Colors
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Forecast
import com.weatherapp.core.model.Location
import com.weatherapp.ui.forecast.store.ForecastStore
import com.weatherapp.ui.theme.ColorOnPrimaryContainer
import com.weatherapp.ui.theme.ColorOnSecondary
import com.weatherapp.ui.theme.ColorPrimaryContainer
import com.weatherapp.ui.theme.ColorSecondary
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastContent(
    state: ForecastStore.State,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "${state.dateTime.dayOfMonth} ${
                            state.dateTime.dayOfWeek.getDisplayName(
                                TextStyle.FULL,
                                Locale.getDefault()
                            )
                        }",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onCloseClick
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close the Forecast screen"
                        )
                    }
                }
            )
        }
    ) {
        Column(
            modifier = modifier.fillMaxSize().padding(it)
        ) {
            when {
                state.forecasts.isNotEmpty() -> ShowForecastForDay(state.forecasts.first().location,state.forecasts)
                state.isLoading ->  CircularProgressIndicator(modifier = modifier.align(CenterHorizontally))
                else -> ForecastError(modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun ForecastError(modifier: Modifier = Modifier) {
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
fun ShowForecastForDay(
    location: Location,
    forecasts: List<Forecast> = emptyList(),
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 8.dp)
    ) {
        ForecastLocationSection(location,modifier)
        val datesSortedByHour = forecasts.map { it.getLocalDateTime() }.sortedBy { it.hour }
        var selectedHour by rememberSaveable{ mutableStateOf<Int?>(null) }
        LazyRow(
            modifier = modifier.fillMaxWidth().wrapContentHeight()
        ) {
            items(datesSortedByHour) {
                val buttonColor = selectedHour?.let { hour ->
                    if(hour == it.hour) {
                        ColorSecondary
                    } else {
                        ColorPrimaryContainer
                    }
                }
                val textColor = selectedHour?.let { hour ->
                    if(hour == it.hour) {
                        ColorOnSecondary
                    } else {
                        ColorOnPrimaryContainer
                    }
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = buttonColor ?: ColorPrimaryContainer,
                    ),
                    onClick = {
                        selectedHour = it.hour
                    },
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "${if(it.hour < 10) "0" else ""}${it.hour}:${if(it.minute < 10) "0" else ""}${it.minute}",
                        color = textColor?: ColorOnPrimaryContainer
                    )
                }
            }
        }

        if(selectedHour != null) {
            val selectedForecast = forecasts.first { it.getLocalDateTime().hour == selectedHour }
            ForecastHourSection(
                forecast = selectedForecast,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}