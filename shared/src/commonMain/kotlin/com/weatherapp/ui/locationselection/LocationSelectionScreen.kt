package com.weatherapp.ui.locationselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Location
import com.weatherapp.ui.locationselection.component.LocationForecastContent
import com.weatherapp.ui.locationselection.component.LocationSelectionContent
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import com.weatherapp.ui.settings.SettingsComponent
import com.weatherapp.ui.settings.component.SettingsPaneContent
import com.weatherapp.ui.settings.store.SettingsStore
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

@Composable
fun LocationSelectionScreen(
    settingsComponent: SettingsComponent,
    locationSelectionComponent: LocationSelectionComponent,
    onForecastForLocationClicked: (Location, LocalDateTime) -> Unit
) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val locationQuerySelectionState by locationSelectionComponent.state.collectAsState()
    val locationSelectionLabelState by locationSelectionComponent.getIncomingLabelFlow(scope).collectAsState()
    val settingsLabelState by settingsComponent.getIncomingLabelFlow(scope).collectAsState()

    when(locationSelectionLabelState) {
        is LocationSelectionStore.Label.CurrentLocationChosen -> {
            val currentLocationChosenLabel = locationSelectionLabelState as LocationSelectionStore.Label.CurrentLocationChosen
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
            val currentLocationChoosingError = locationSelectionLabelState as LocationSelectionStore.Label.CurrentLocationChoosingError
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

    val settingsState by settingsComponent.state.collectAsState()

    if(!settingsState.isError
        && !settingsState.isLoading
        && locationQuerySelectionState.locationForecasts.isNotEmpty()
        && settingsLabelState == SettingsStore.Label.SettingsUpdatedLabel) {
        locationSelectionComponent.onEvent(LocationSelectionStore.Intent.ShowForecastsForLocation(
            locationQuerySelectionState.locationForecasts.first().location
        ))
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
                    SettingsPaneContent(
                        settingsState,
                        settingsComponent::onEvent,
                    )
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
                    locationSelectionState = locationQuerySelectionState,
                    settingsState = settingsState,
                    onItemClicked = onForecastForLocationClicked,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxSize()
                )
            }
        }
    }

}