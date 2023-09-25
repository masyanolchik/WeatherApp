package com.weatherapp.ui.settings.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.core.model.WeatherUnit
import com.weatherapp.ui.settings.store.SettingsStore

@Composable
fun RowScope.SettingsPaneContent(
    state: SettingsStore.State,
    onEvent: (SettingsStore.Intent) -> Unit = {},
    modifier: Modifier = Modifier
) {
    when {
        state.isLoading -> {
            CircularProgressIndicator(modifier.fillMaxWidth())
        }
        state.isError -> {
            Box(modifier = modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.padding(4.dp))
                    Text(state.errorMessage)
                }
            }
        }
        else -> {
            Counter(
                onDecrementButtonClickListener = {
                    if(isNumericToX(it)) {
                        onEvent(SettingsStore.Intent.DecrementTempDifference)
                    }
                },
                onIncrementButtonClickListener = {
                    if(isNumericToX(it)) {
                        onEvent(SettingsStore.Intent.IncrementTempDifference)
                    }
                },
                onTextFieldValueChange = {
                    if(isNumericToX(it)) {
                        onEvent(SettingsStore.Intent.ChangeTempDifference(it.toInt()))
                    }
                },
                textFieldValue = state.currentTempDifference.toString(),
                modifier = modifier
            )
            WeatherUnitButton(
                text = "C",
                onButtonClick = {
                    onEvent(SettingsStore.Intent.ChangeWeatherUnit(WeatherUnit.METRIC))
                },
                isSelected = state.currentWeatherUnit == WeatherUnit.METRIC,
                modifier = modifier
            )

            WeatherUnitButton(
                text = "F",
                onButtonClick = {
                    onEvent(SettingsStore.Intent.ChangeWeatherUnit(WeatherUnit.IMPERIAL))
                },
                isSelected = state.currentWeatherUnit == WeatherUnit.IMPERIAL,
                modifier = modifier
            )
        }
    }

}

fun isNumericToX(toCheck: String): Boolean {
    return toCheck.toDoubleOrNull() != null
}