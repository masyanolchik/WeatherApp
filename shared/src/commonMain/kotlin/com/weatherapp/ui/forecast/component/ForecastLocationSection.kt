package com.weatherapp.ui.forecast.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Location

@Composable
fun ForecastLocationSection(
    location: Location,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Text(
            text = location.name,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = location.state,
            style = MaterialTheme.typography.subtitle1
        )
        Text(
            text = location.country,
            style = MaterialTheme.typography.body2
        )
    }
}