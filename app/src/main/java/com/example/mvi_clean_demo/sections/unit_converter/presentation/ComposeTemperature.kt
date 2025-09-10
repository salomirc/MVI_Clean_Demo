package com.example.mvi_clean_demo.sections.unit_converter.presentation

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.common.ui_components.ComposeLifecycleEvent
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.Convert
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.SetScale
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.SetTemperature
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.theme.clientTierInitialsSurface
import com.example.mvi_clean_demo.theme.disabled

@Composable
fun ComposeTemperature(
    model: TemperatureViewModel.Model,
    sendEvent: (TemperatureViewModel.Event) -> Unit,
    processEvent: suspend (TemperatureViewModel.Event) -> Unit
) {
    val celsiusString = stringResource(id = R.string.celsius)
    val fahrenheitString = stringResource(id = R.string.fahrenheit)
    val inputScaleString = remember(model.scale) {
        if (model.scale == R.string.celsius) fahrenheitString else celsiusString
    }
    val resultString = remember(model.convertedValue) {
        val scaleString = if (model.scale == R.string.celsius) celsiusString else fahrenheitString
        model.convertedValue?.let {"$it $scaleString" }
    }
    LaunchedEffect(model.temperature, model.scale) {
        processEvent(Convert(model.temperature, model.scale))
    }
    // Remember the scroll state
    val scrollState = rememberScrollState()

    ComposeLifecycleEvent(
        onResume = {
            sendEvent(TemperatureViewModel.Event.GetData)
        }
    )

    Surface {
        if (model.isLoading) {
            TemperaturePlaceholder()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TemperatureTextField(
                        temperature = model.temperature,
                        modifier = Modifier
                            .padding(start = 40.dp)
                            .fillMaxWidth(0.8f),
                        onValueChange = { text ->
                            sendEvent(SetTemperature(temperature = text))
                        },
                        onDone = { },
                    )
                    Text(
                        text = inputScaleString,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .width(32.dp)
                    )
                }
                TemperatureScaleButtonGroup(
                    scale = model.scale,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.clientTierInitialsSurface,
                            shape = CircleShape
                        )
                        .border(
                            border = BorderStroke(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            ),
                            shape = CircleShape
                        ),
                    onClick = { stringResId ->
                        sendEvent(SetScale(scale = stringResId))
                    }
                )
                resultString?.let { text ->
                    Text(
                        text = text,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

@Composable
fun TemperatureTextField(
    temperature: String,
    modifier: Modifier = Modifier,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = temperature,
        onValueChange = onValueChange,
        label = {
            Text(text = stringResource(R.string.temperature))
        },
        placeholder = {
            Text(
                text = stringResource(id = R.string.placeholder_temperature)
            )
        },
        modifier = modifier,
        keyboardActions = KeyboardActions(
            onDone = {
                onDone()
                focusManager.clearFocus()
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        colors = TextFieldDefaults.colors().copy(
            focusedPlaceholderColor = MaterialTheme.colorScheme.disabled,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.disabled
        )
    )
}

@Composable
fun TemperatureScaleButtonGroup(
    scale: Int,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(modifier = modifier) {
        TemperatureRadioButton(
            scale = scale,
            resId = R.string.celsius,
            onClick = onClick
        )
        TemperatureRadioButton(
            scale = scale,
            resId = R.string.fahrenheit,
            onClick = onClick,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun TemperatureRadioButton(
    scale: Int,
    @StringRes resId: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RadioButton(
            selected = scale == resId,
            onClick = {
                onClick(resId)
            },
            colors = RadioButtonDefaults.colors(
                unselectedColor = MaterialTheme.colorScheme.disabled
            )
        )
        Text(
            text = stringResource(resId),
            modifier = Modifier
        )
    }
}

@LightDarkPreview
@Composable
fun TemperatureConverterPreview() {
    val model = TemperatureViewModel.Model(
        isLoading = false,
        temperature = "100",
        scale = R.string.celsius,
        convertedValue = 123.34F
    )
    ComposeUnitConverterTheme {
        Surface {
            ComposeTemperature(
                model = model,
                sendEvent = {},
                processEvent = {}
            )
        }
    }
}

@LightDarkPreview
@Composable
fun TemperatureConverterLoadingPreview() {
    val model = TemperatureViewModel.Model(
        isLoading = true,
        temperature = "100",
        scale = R.string.celsius,
        convertedValue = 123.34F
    )
    ComposeUnitConverterTheme {
        Surface {
            ComposeTemperature(
                model = model,
                sendEvent = {},
                processEvent = {}
            )
        }
    }
}