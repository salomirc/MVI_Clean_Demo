package com.example.mvi_clean_demo.screens

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.mvi_clean_demo.ui.theme.getDisabledColor
import com.example.mvi_clean_demo.viewmodels.ITemperatureViewModel
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel

@Composable
fun TemperatureConverter(viewModel: ITemperatureViewModel) {
    val isDarkTheme = isSystemInDarkTheme()
    val strCelsius = stringResource(id = R.string.celsius)
    val strFahrenheit = stringResource(id = R.string.fahrenheit)
    val temperature by viewModel.temperature.collectAsStateWithLifecycle()
    val convertedValue by viewModel.convertedValue.collectAsStateWithLifecycle()
    val conversionScale by viewModel.scale.collectAsStateWithLifecycle()
    val inputScale by remember(conversionScale) {
        mutableIntStateOf(
            when (conversionScale) {
                R.string.celsius -> R.string.fahrenheit
                else -> R.string.celsius
            }
        )
    }
    val isButtonEnabled by remember {
        derivedStateOf { viewModel.isButtonEnabled(temperature) }
    }
    val result by remember(convertedValue) {
        val scaleString = if (conversionScale == R.string.celsius) strCelsius else strFahrenheit
        mutableStateOf(convertedValue?.let { "$convertedValue $scaleString" })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row {
            TemperatureTextField(
                isDarkTheme = isDarkTheme,
                temperature = temperature,
                modifier = Modifier.padding(bottom = 16.dp),
                onValueChange = { s ->
                    viewModel.setTemperature(s)
                },
                onDone = {
                    viewModel.convert(temperature, conversionScale)
                },
            )
            Text(
                text = stringResource(inputScale),
                modifier = Modifier
                    .padding(start = 8.dp)
            )
        }
        TemperatureScaleButtonGroup(
            scale = conversionScale,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = { stringResId ->
                viewModel.setScale(stringResId)
            }
        )
        Button(
            onClick = { viewModel.convert(temperature, conversionScale) },
            enabled = isButtonEnabled
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
    isDarkTheme: Boolean,
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
                text = stringResource(id = R.string.placeholder_temperature),
                color = MaterialTheme.colorScheme.getDisabledColor(isDarkTheme)
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
        singleLine = true
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