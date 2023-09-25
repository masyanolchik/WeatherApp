package com.weatherapp.ui.settings.component

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Counter(
    onDecrementButtonClickListener: (String) -> Unit = {},
    onIncrementButtonClickListener: (String) -> Unit = {},
    textFieldValue: String = "0",
    onTextFieldValueChange: (String) -> Unit = {},
    decrementButtonContentDescription: String = "Reduce temperature difference",
    incrementButtonContentDescription: String = "Add temperature difference",
    modifier: Modifier = Modifier,
) {
    val counterState by rememberSaveable{ mutableStateOf(textFieldValue) }
    IconButton(
        onClick = {
            onDecrementButtonClickListener(counterState)
        },
        modifier = modifier.padding(horizontal = 2.dp, vertical = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = decrementButtonContentDescription
        )
    }
    TextField(
        value = textFieldValue,
        onValueChange = onTextFieldValueChange,
        modifier = modifier.padding(horizontal = 2.dp, vertical = 16.dp).width(64.dp).height(48.dp)
    )
    IconButton(
        onClick = {
            onIncrementButtonClickListener(counterState)
        },
        modifier = modifier.padding(horizontal = 2.dp, vertical = 16.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = incrementButtonContentDescription
        )
    }
}