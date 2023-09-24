package com.weatherapp.ui.locationselection.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Location

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchBar(
    query: String = "",
    onQueryChange: (String) -> Unit = { _ -> },
    onLocationClick: (Location) -> Unit = { _ -> },
    locationList: List<Location> = emptyList(),
    errorHasOccurred: Boolean = false,
    errorMessage: String = "",
    isLoading: Boolean = false,
    isActive: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var text by rememberSaveable { mutableStateOf(query) }

    Box(modifier.semantics { isTraversalGroup = true }) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter),
            query = text,
            onQueryChange = {
                text = it
                onQueryChange(text)
            },
            onSearch = { },
            active = isActive,
            onActiveChange = {},
            placeholder = {
                Text("Search: London or 61000,UA for a ZIP code")
            },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        ) {
            when {
                errorHasOccurred -> {
                    Box(modifier = modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.padding(4.dp))
                            Text(errorMessage)
                        }
                    }
                }
                isLoading -> {
                    Box(modifier = modifier.fillMaxWidth()) {
                       CircularProgressIndicator(
                           modifier = Modifier.align(Alignment.Center)
                       )
                    }
                }
                else -> LazyColumn {
                    items(locationList) {
                        LocationItem(onLocationClick, it)
                    }
                }
            }
        }
    }
}