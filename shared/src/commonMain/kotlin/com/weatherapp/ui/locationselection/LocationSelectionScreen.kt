package com.weatherapp.ui.locationselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.ui.locationselection.component.LocationForecastContent
import com.weatherapp.ui.locationselection.component.LocationSelectionContent
import com.weatherapp.ui.locationselection.store.LocationSelectionStore
import kotlinx.coroutines.launch

@Composable
fun LocationSelectionScreen(locationSelectionComponent: LocationSelectionComponent) {
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
                    onEvent = locationSelectionComponent::onEvent,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxSize()
                )
            }
            LocationSelectionContent(
                state = locationQuerySelectionState,
                onEvent = locationSelectionComponent::onEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp)
                    .align(Alignment.TopCenter)
            )
        }
    }

}