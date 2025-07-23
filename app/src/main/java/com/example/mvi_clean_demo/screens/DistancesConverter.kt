package com.example.mvi_clean_demo.screens

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.di.AppContainer
import com.example.mvi_clean_demo.screens.NavigationItemModel.Destination
import com.example.mvi_clean_demo.ui.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel

@Composable
fun DistancesConverter(viewModel: DistancesViewModel, navController: NavHostController) {
    val strMeter = stringResource(id = R.string.meter)
    val strMile = stringResource(id = R.string.mile)
    val distance by viewModel.distance.collectAsStateWithLifecycle()
    val unit by viewModel.unit.collectAsStateWithLifecycle()
    val convertedValue by viewModel.convertedDistance.collectAsStateWithLifecycle()
    val result by remember(convertedValue) {
        mutableStateOf(
            if (convertedValue.isNaN())
                ""
            else
                "$convertedValue ${
                    if (unit == R.string.meter)
                        strMile
                    else strMeter
                }"
        )
    }
    val calc = {
        viewModel.convert(distance)
    }
    val enabled by remember(distance) {
        mutableStateOf(viewModel.isButtonEnabled(distance))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DistanceTextField(
            distance = distance,
            modifier = Modifier.padding(bottom = 16.dp),
            callback = calc,
            viewModel = viewModel
        )
        DistanceScaleButtonGroup(
            selected = unit,
            modifier = Modifier.padding(bottom = 16.dp)
        ) { resId: Int ->
            viewModel.setUnit(resId)
        }
        Button(
            onClick = calc,
            enabled = enabled
        ) {
            Text(text = stringResource(id = R.string.convert))
        }
        if (result.isNotEmpty()) {
            Text(
                text = result,
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
                onClick = {
                    navController.navigate(Destination.TemperatureDestination("200"))
                },
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
    distance: String,
    modifier: Modifier = Modifier,
    callback: () -> Unit,
    viewModel: DistancesViewModel
) {
    TextField(
        value = distance,
        onValueChange = {
            viewModel.setDistance(it)
        },
        placeholder = {
            Text(text = stringResource(id = R.string.placeholder_distance))
        },
        modifier = modifier,
        keyboardActions = KeyboardActions(onAny = {
            callback()
        }),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        singleLine = true
    )
}

@Composable
fun DistanceScaleButtonGroup(
    selected: Int,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit
) {
    Row(modifier = modifier) {
        DistanceRadioButton(
            selected = selected == R.string.meter,
            resId = R.string.meter,
            onClick = onClick
        )
        DistanceRadioButton(
            selected = selected == R.string.mile,
            resId = R.string.mile,
            onClick = onClick,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun DistanceRadioButton(
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
            }
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
fun DistancesConverterPreview() {
    val appContainer = AppContainer(LocalContext.current.applicationContext)
    val navController = rememberNavController()
    ComposeUnitConverterTheme {
        Surface {
            DistancesConverter(
                viewModel = viewModel<DistancesViewModel>(factory = appContainer.provideDistancesViewModelFactory()),
                navController = navController
            )
        }
    }
}

