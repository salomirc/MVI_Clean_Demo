package com.example.mvi_clean_demo.common.ui_components.unit_converter

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mvi_clean_demo.common.ui_components.unit_converter.NavigationItemModel.Distance
import com.example.mvi_clean_demo.common.ui_components.unit_converter.NavigationItemModel.Temperature
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.DistancesNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.TemperatureNavTarget
import com.example.mvi_clean_demo.sections.unit_converter.presentation.ComposeDistances
import com.example.mvi_clean_demo.sections.unit_converter.presentation.ComposeTemperature
import com.example.mvi_clean_demo.sections.unit_converter.presentation.DistancesViewModel
import com.example.mvi_clean_demo.MainViewModel.Event
import com.example.mvi_clean_demo.MainViewModel.Event.SetNavigationTitle
import com.example.mvi_clean_demo.sections.unit_converter.presentation.TemperatureViewModel
import kotlinx.serialization.Serializable

sealed interface UnitConverterNavTarget {
    @Serializable
    data class TemperatureNavTarget(val initialTempValue: String): UnitConverterNavTarget
    @Serializable
    data object DistancesNavTarget : UnitConverterNavTarget
}

@Composable
fun ComposeUnitConverterNavHost(
    sendEvent: (Event) -> Unit,
    navController: NavHostController,
    modifier: Modifier,
    onNextButton: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Temperature.navTarget,
        modifier = modifier
    ) {
        composable<TemperatureNavTarget> { backStackEntry  ->
            val initialTempValue = backStackEntry.toRoute<TemperatureNavTarget>().initialTempValue
            val viewModel: TemperatureViewModel = hiltViewModel(
                creationCallback = { factory: TemperatureViewModel.Factory ->
                    factory.create(initialTempValue) // Provide the initial temperature
                })
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = Temperature.label)))
            ComposeTemperature(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                }
            )
            LogNavigation(backStackEntry, viewModel)
        }

        composable<DistancesNavTarget> { backStackEntry ->
            val viewModel: DistancesViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = Distance.label)))
            ComposeDistances(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNextButton = onNextButton
            )
            LogNavigation(backStackEntry, viewModel)
        }
    }
}

@Composable
fun <T: ViewModel> LogNavigation(backStackEntry: NavBackStackEntry, viewModel: T) {
    LaunchedEffect(backStackEntry) {
        Log.d("Navigation", "NavBackStackEntry = ${backStackEntry.destination.route}")
        Log.d("Navigation", "ViewModel = $viewModel")
    }
}