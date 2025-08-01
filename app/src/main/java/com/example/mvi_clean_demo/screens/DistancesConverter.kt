package com.example.mvi_clean_demo.screens

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.mvi_clean_demo.ui.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.ui.theme.disabled
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel.Event.Convert
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel.Event.ValidateButtonEnabled

@Composable
fun DistancesConverter(
    model: DistancesViewModel.Model,
    sendEvent: (DistancesViewModel.Event) -> Unit,
    onNextButton: () -> Unit
) {
    val strMeter = stringResource(id = R.string.meter)
    val strMile = stringResource(id = R.string.mile)
    val inputUnit by remember(model.unit) {
        mutableIntStateOf(
            when (model.unit) {
                R.string.meter -> R.string.mile
                else -> R.string.meter
            }
        )
    }
    val result by remember(model.convertedValue) {
        val scaleString = if (model.unit == R.string.meter) strMeter else strMile
        mutableStateOf(model.convertedValue?.let { value ->
            "$value $scaleString"
        })
    }
    LaunchedEffect(model.distance) {
        sendEvent(ValidateButtonEnabled(model.distance))
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
            DistanceTextField(
                temperature = model.distance,
                modifier = Modifier.padding(start = 28.dp),
                onValueChange = { text ->
                    sendEvent(DistancesViewModel.Event.SetDistance(distance = text))
                },
                onDone = {
                    sendEvent(Convert(model.distance, model.unit))
                },
            )
            Text(
                text = stringResource(inputUnit),
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(20.dp)
            )
        }
        DistanceUnitButtonGroup(
            unit = model.unit,
            modifier = Modifier.padding(bottom = 16.dp),
            onClick = { stringResId ->
                sendEvent(DistancesViewModel.Event.SetUnit(unit = stringResId))
            }
        )
        Button(
            onClick = { sendEvent(Convert(model.distance, model.unit)) },
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
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = onNextButton,
                modifier = Modifier.defaultMinSize(128.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.next)
                )
            }
        }
    }
}

@Composable
fun DistanceTextField(
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
fun DistanceUnitButtonGroup(
    unit: Int,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(modifier = modifier) {
        DistanceRadioButton(
            unit = unit,
            resId = R.string.meter,
            onClick = onClick
        )
        DistanceRadioButton(
            unit = unit,
            resId = R.string.mile,
            onClick = onClick,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun DistanceRadioButton(
    unit: Int,
    @StringRes resId: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        RadioButton(
            selected = unit == resId,
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
fun DistanceConverterPreview() {
    val model = DistancesViewModel.Model(
        distance = "100",
        unit = R.string.meter,
        isButtonEnabled = false
    )
    ComposeUnitConverterTheme {
        Surface {
            DistancesConverter(
                model = model,
                sendEvent = { },
                onNextButton = { }
            )
        }
    }
}

