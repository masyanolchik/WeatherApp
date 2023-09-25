package com.weatherapp.ui.settings.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.weatherapp.ui.theme.ColorPrimary

@Composable
fun WeatherUnitButton(
    text: String = "",
    isSelected: Boolean = false,
    onButtonClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val buttonColors = if(isSelected) ButtonDefaults.buttonColors() else ButtonDefaults.outlinedButtonColors(contentColor = ColorPrimary)
    Button(
        onClick = {
            onButtonClick()
        },
        border = BorderStroke(1.dp, ColorPrimary),
        colors = buttonColors,
        shape = RoundedCornerShape(20.dp),
        modifier = modifier.padding(horizontal = 2.dp, vertical = 16.dp)) {
        Text(text)
    }
}