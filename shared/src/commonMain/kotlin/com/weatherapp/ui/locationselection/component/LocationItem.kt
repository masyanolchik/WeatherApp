package com.weatherapp.ui.locationselection.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Location

@Composable
fun LocationItem(
    onClick: (Location) -> Unit,
    location: Location,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "${location.name}, ${location.state}${location.zip}, ${location.country}",
            style = MaterialTheme.typography.subtitle1,
            modifier = modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable {
                    onClick(location)
                }
        )
    }

}