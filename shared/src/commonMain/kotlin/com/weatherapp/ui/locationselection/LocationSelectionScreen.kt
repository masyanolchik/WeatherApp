package com.weatherapp.ui.locationselection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Location
import com.weatherapp.ui.locationselection.component.LocationForecastContent
import com.weatherapp.ui.locationselection.component.LocationSelectionContent
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.theme.ColorPrimary
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@Composable
fun LocationSelectionScreen(
    locationSelectionComponent: LocationSelectionComponent,
    onForecastForLocationClicked: (Location, LocalDateTime) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val locationQuerySelectionState by locationSelectionComponent.state.collectAsState()
    val labelState by locationSelectionComponent.getIncomingLabelFlow(scope).collectAsState()

    when(labelState) {
        is LocationSelectionStore.Label.CurrentLocationChosen -> {
            val currentLocationChosenLabel = labelState as LocationSelectionStore.Label.CurrentLocationChosen
            scope.launch {
                locationSelectionComponent.onEvent(LocationSelectionStore.Intent.ShowForecastsForLocation(currentLocationChosenLabel.location))
                snackbarHostState
                    .showSnackbar(
                        message = "Location ${currentLocationChosenLabel.location.name} is chosen",
                        duration = SnackbarDuration.Short,
                    )
            }
        }
        is LocationSelectionStore.Label.CurrentLocationChoosingError -> {
            val currentLocationChoosingError = labelState as LocationSelectionStore.Label.CurrentLocationChoosingError
            scope.launch {
                snackbarHostState
                    .showSnackbar(
                        message = currentLocationChoosingError.errorMessage,
                        duration = SnackbarDuration.Short,
                    )
            }
        }
        LocationSelectionStore.Label.NoLocationChosen -> {}
    }

    Scaffold(
        topBar = {
            Row{
                LocationSelectionContent(
                    state = locationQuerySelectionState,
                    onEvent = locationSelectionComponent::onEvent,
                    modifier = Modifier
                        .height(200.dp)
                        .padding(bottom = 16.dp)
                        .weight(2f)
                )
                if(!locationQuerySelectionState.isSearchActive) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 16.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Reduce temperature difference"
                        )
                    }
                    TextField(
                        value = "0",
                        onValueChange = {},
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 16.dp).width(48.dp).height(48.dp)
                    )
                    IconButton(
                        onClick = {},
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 16.dp),
                        ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add temperature difference"
                        )
                    }
                    Button(
                        onClick = {

                        },
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 16.dp),
                        shape = RoundedCornerShape(20.dp)) {
                        Text("C")
                    }
                    Button(
                        onClick = {

                        },
                        border = BorderStroke(1.dp, ColorPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorPrimary),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 16.dp)) {
                        Text("F")
                    }
                    IconButton(onClick = {
                        if(locationQuerySelectionState.locationForecasts.isNotEmpty()) {
                            locationSelectionComponent.onEvent(
                                LocationSelectionStore.Intent.ShowForecastsForLocation(
                                    locationQuerySelectionState.locationForecasts.first().location)
                            )
                        }
                    },
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh"
                        )
                    }
                }

            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = if(locationQuerySelectionState.isSearchActive) 200.dp else 80.dp)
            ) {
                LocationForecastContent(
                    state = locationQuerySelectionState,
                    onItemClicked = onForecastForLocationClicked,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxSize()
                )
            }
        }
    }

}