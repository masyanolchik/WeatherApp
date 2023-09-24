package com.weatherapp.ui.forecast.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.Forecast
import com.weatherapp.ui.AsyncImage

@Composable
fun ForecastHourSection(
    forecast: Forecast,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(8.dp)) {
        Row {
            Column(Modifier.weight(1f).fillMaxWidth()) {
                Text(
                    text = forecast.weatherTitle
                )
                Text(
                    text = forecast.weatherDescription
                )
                Text(
                    text = "${forecast.temperature}C",
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(
                    text = "Feels like ${forecast.feelsLikeTemperature}C"
                )
                Text(
                    text = "Humidity: ${forecast.humidityPercentage}%"
                )
                Text(
                    text = "Cloudiness: ${forecast.cloudinessPercentage}%"
                )
                Text(
                    text = "Wind speed: ${forecast.windSpeed} M/s"
                )
                Text(
                    text = "Wind direction: ${forecast.windDirectionDegrees} degrees"
                )
                Text(
                    text = "Wind gust: ${forecast.windGust} M/s"
                )
                Text(
                    text = "Visibility: ${forecast.visibilityMeters} metres"
                )
            }
            Column(Modifier.weight(1f).fillMaxWidth()) {
                AsyncImage(
                    url = forecast.weatherIconUrl,
                    contentDescription = forecast.weatherTitle,
                    modifier = Modifier.size(120.dp).align(End)
                )
            }
        }
    }
}