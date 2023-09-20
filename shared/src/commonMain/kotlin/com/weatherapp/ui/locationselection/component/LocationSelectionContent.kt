package com.weatherapp.ui.locationselection.component

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Location
import com.weatherapp.ui.locationselection.store.LocationSelectionStore

@Composable
fun LocationSelectionContent(
    state: LocationSelectionStore.State,
    onEvent:(LocationSelectionStore.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    when(state) {
        is LocationSelectionStore.State.Error ->
            StatefulLocationSearchBar(
                state = state,
                onEvent = onEvent,
                errorHasOccurred = true,
                errorMessage = state.errorMessage,
                modifier = modifier,
            )
        is LocationSelectionStore.State.FoundLocations ->
            StatefulLocationSearchBar(
                state = state,
                onEvent = onEvent,
                locationList = state.locations,
                modifier = modifier,
            )
        is LocationSelectionStore.State.Loading ->
            StatefulLocationSearchBar(
                state = state,
                onEvent = onEvent,
                isLoading = true,
                modifier = modifier,
            )
    }
}

@Composable
fun StatefulLocationSearchBar(
    state: LocationSelectionStore.State,
    onEvent:(LocationSelectionStore.Intent) -> Unit,
    locationList: List<Location> = emptyList(),
    errorHasOccurred: Boolean = false,
    errorMessage: String = "",
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    LocationSearchBar(
        modifier = modifier.padding(6.dp),
        query = state.searchQuery,
        onQueryChange = {
            onEvent(LocationSelectionStore.Intent.EnterSearchQueryIntent(it))
        },
        onLocationClick = {
            onEvent(LocationSelectionStore.Intent.ChoseCurrentLocation(it))
        },
        locationList = locationList,
        errorHasOccurred = errorHasOccurred,
        errorMessage = errorMessage,
        isLoading = isLoading
    )
}

