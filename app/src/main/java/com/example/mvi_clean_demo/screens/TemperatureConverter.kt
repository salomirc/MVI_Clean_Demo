package com.example.mvi_clean_demo.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.di.AppContainer
import com.example.mvi_clean_demo.ui.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.viewmodels.ITemperatureViewModel
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel

@Composable
fun TemperatureConverter(viewModel: ITemperatureViewModel) {
    val strCelsius = stringResource(id = R.string.celsius)
    val strFahrenheit = stringResource(id = R.string.fahrenheit)
    val temperature by viewModel.temperature.collectAsStateWithLifecycle()
    val convertedValue by viewModel.convertedValue.collectAsStateWithLifecycle()
    val scale by viewModel.scale.collectAsStateWithLifecycle()
//    val isButtonEnabled by remember(temperature) {
//        mutableStateOf(viewModel.isButtonEnabled(temperature))
//    }
    val isButtonEnabled by remember {
        derivedStateOf { viewModel.isButtonEnabled(temperature) }
    }
    val result by remember(convertedValue) {
        mutableStateOf(
            if (convertedValue.isNaN())
                ""
            else
                "$convertedValue ${
                    if (scale == R.string.celsius)
                        strCelsius
                    else strFahrenheit
                }"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TemperatureTextField(
            temperature = temperature,
            modifier = Modifier.padding(bottom = 16.dp),
            callback = { viewModel.convert(temperature, scale) },
            viewModel = viewModel
        )
        TemperatureScaleButtonGroup(
            selected = scale,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = { resId: Int ->
                viewModel.setScale(resId)
            }
        )
        Button(
            onClick = { viewModel.convert(temperature, scale) },
            enabled = isButtonEnabled
        ) {
            Text(text = stringResource(id = R.string.convert))
        }
        if (result.isNotEmpty()) {
            Text(
                text = result,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}

@Composable
fun TemperatureTextField(
    temperature: String,
    modifier: Modifier = Modifier,
    callback: () -> Unit,
    viewModel: ITemperatureViewModel
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = temperature,
        onValueChange = {
            viewModel.setTemperature(it)
        },
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder))
        },
        modifier = modifier,
        keyboardActions = KeyboardActions(
            onDone = {
                callback()
                focusManager.clearFocus()
            }
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@Composable
fun TemperatureScaleButtonGroup(
    selected: Int,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(modifier = modifier) {
        TemperatureRadioButton(
            selected = selected == R.string.celsius,
            resId = R.string.celsius,
            onClick = onClick
        )
        TemperatureRadioButton(
            selected = selected == R.string.fahrenheit,
            resId = R.string.fahrenheit,
            onClick = onClick,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun TemperatureRadioButton(
    selected: Boolean,
    resId: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RadioButton(
            selected = selected,
            onClick = {
                onClick(resId)
            },
            colors = RadioButtonDefaults.colors(
                unselectedColor = Color(0xFFC7C7C7)
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
    val appContainer = AppContainer(LocalContext.current.applicationContext)
    ComposeUnitConverterTheme {
        Surface {
            TemperatureConverter(
                viewModel = viewModel<TemperatureViewModel>(
                    factory = appContainer.provideTemperatureViewModelFactory("100")
                )
            )
        }
    }
}