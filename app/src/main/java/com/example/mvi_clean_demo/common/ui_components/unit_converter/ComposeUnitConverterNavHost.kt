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
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.citizen.presentation.UsersViewModel
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.DistancesNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.TemperatureNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.UsersNavTarget
import com.example.mvi_clean_demo.screens.DistancesConverter
import com.example.mvi_clean_demo.screens.NavigationItemModel.Distance
import com.example.mvi_clean_demo.screens.NavigationItemModel.Temperature
import com.example.mvi_clean_demo.screens.TemperatureConverter
import com.example.mvi_clean_demo.screens.UsersScreen
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel
import com.example.mvi_clean_demo.viewmodels.MainViewModel.Event
import com.example.mvi_clean_demo.viewmodels.MainViewModel.Event.SetNavigationTitle
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel
import kotlinx.serialization.Serializable

sealed interface UnitConverterNavTarget {
    @Serializable
    data class TemperatureNavTarget(val initialTempValue: String): UnitConverterNavTarget
    @Serializable
    data object DistancesNavTarget : UnitConverterNavTarget
    @Serializable
    data object UsersNavTarget: UnitConverterNavTarget
}

@Composable
fun ComposeUnitConverterNavHost(
    sendEvent: (Event) -> Unit,
    navController: NavHostController,
    modifier: Modifier
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
            TemperatureConverter(
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
            DistancesConverter(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNextButton = {
                    navController.navigate(UsersNavTarget)
                }
            )
            LogNavigation(backStackEntry, viewModel)
        }

        composable<UsersNavTarget> { backStackEntry ->
            val viewModel: UsersViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = R.string.users_screen_title)))
            UsersScreen(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNextButton = {
                    navController.navigate(UsersNavTarget)
                }
            )
            LogNavigation(backStackEntry, viewModel)
        }
    }
}

@Composable
private fun <T: ViewModel> LogNavigation(backStackEntry: NavBackStackEntry, viewModel: T) {
    LaunchedEffect(backStackEntry) {
        Log.d("Navigation", "NavBackStackEntry = ${backStackEntry.destination.route}")
        Log.d("Navigation", "ViewModel = $viewModel")
    }
}