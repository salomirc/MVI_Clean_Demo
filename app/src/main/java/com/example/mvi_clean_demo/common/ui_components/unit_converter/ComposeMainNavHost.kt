package com.example.mvi_clean_demo.common.ui_components.unit_converter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.citizen.presentation.UsersViewModel
import com.example.mvi_clean_demo.common.ui_components.unit_converter.MainNavTarget.ConverterTabbedNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.MainNavTarget.UsersNavTarget
import com.example.mvi_clean_demo.screens.UsersScreen
import com.example.mvi_clean_demo.viewmodels.MainViewModel
import com.example.mvi_clean_demo.viewmodels.MainViewModel.Event
import com.example.mvi_clean_demo.viewmodels.MainViewModel.Event.SetNavigationTitle
import kotlinx.serialization.Serializable

sealed interface MainNavTarget {
    @Serializable
    data object ConverterTabbedNavTarget: MainNavTarget
    @Serializable
    data object UsersNavTarget: MainNavTarget
}

@Composable
fun ComposeMainNavHost(
    mainModel: MainViewModel.Model,
    sendEvent: (Event) -> Unit,
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ConverterTabbedNavTarget
    ) {
        composable<ConverterTabbedNavTarget> { backStackEntry  ->
            UnitConverterTabbedScreen(
                model = mainModel,
                sendEvent = sendEvent,
                onNavigateBack = onNavigateBack,
                onNextButton = {
                    navController.navigate(UsersNavTarget)
                }
            )
        }

        composable<UsersNavTarget> { backStackEntry ->
            val viewModel: UsersViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = R.string.users_screen_title)))
            UsersScreen(
                model = model,
                navigationTitle = mainModel.navigationTitle,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNavigateBack = onNavigateBack,
                onNextButton = { },
            )
            LogNavigation(backStackEntry, viewModel)
        }
    }
}