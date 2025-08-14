package com.example.mvi_clean_demo.sections.unit_converter.presentation

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.theme.disabled
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.Convert
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.SetScale
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.SetTemperature
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel.Event.ValidateButtonEnabled

@Composable
fun ComposeTemperature(
    model: TemperatureViewModel.Model,
    sendEvent: (TemperatureViewModel.Event) -> Unit
) {
    val strCelsius = stringResource(id = R.string.celsius)
    val strFahrenheit = stringResource(id = R.string.fahrenheit)
    val inputScale by remember(model.scale) {
        mutableIntStateOf(
            when (model.scale) {
                R.string.celsius -> R.string.fahrenheit
                else -> R.string.celsius
            }
        )
    }
    val result by remember(model.convertedValue) {
        val scaleString = if (model.scale == R.string.celsius) strCelsius else strFahrenheit
        mutableStateOf(model.convertedValue?.let { value ->
            "$value $scaleString"
        })
    }
    LaunchedEffect(model.temperature) {
        sendEvent(ValidateButtonEnabled(model.temperature))
    }
    // Remember the scroll state
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            TemperatureTextField(
                temperature = model.temperature,
                modifier = Modifier.padding(start = 28.dp),
                onValueChange = { text ->
                    sendEvent(SetTemperature(temperature = text))
                },
                onDone = {
                    sendEvent(Convert(model.temperature, model.scale))
                },
            )
            Text(
                text = stringResource(inputScale),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(20.dp)
            )
        }
        TemperatureScaleButtonGroup(
            scale = model.scale,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = { stringResId ->
                sendEvent(SetScale(scale = stringResId))
            }
        )
        Button(
            onClick = { sendEvent(Convert(model.temperature, model.scale)) },
            enabled = model.isButtonEnabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 3.0.dp
            ),
        ) {
            Text(text = stringResource(id = R.string.convert))
        }
        result?.let { s ->
            Text(
                text = s,
                style = MaterialTheme.typography.headlineSmall
            )
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
            modifier = Modifier.padding(start = 16.dp)
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
                .padding(start = 8.dp)
        )
    }
}


@Preview(
    name = "Light Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = Devices.PIXEL
)
@Preview(
    name = "Dark Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL
)
@Composable
fun TemperatureConverterPreview() {
    val model = TemperatureViewModel.Model(
        temperature = "100",
        scale = R.string.celsius,
        isButtonEnabled = false
    )
    ComposeUnitConverterTheme {
        Surface {
            ComposeTemperature(
                model = model,
                sendEvent = { }
            )
        }
    }
}