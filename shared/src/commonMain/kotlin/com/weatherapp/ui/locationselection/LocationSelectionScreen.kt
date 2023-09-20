package com.weatherapp.ui.locationselection

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.ui.locationselection.component.LocationForecastContent
import com.weatherapp.ui.locationselection.component.LocationSelectionContent

@Composable
fun LocationSelectionScreen(locationSelectionComponent: LocationSelectionComponent) {
    val locationQuerySelectionState by locationSelectionComponent.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        LocationSelectionContent(
            state = locationQuerySelectionState,
            onEvent = locationSelectionComponent::onEvent,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 24.dp)
        ) {
            LocationForecastContent()
        }
    }
}