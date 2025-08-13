package com.example.mvi_clean_demo.common.ui_components.unit_converter

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvi_clean_demo.common.ui_components.unit_converter.MainNavTarget.BlogHostNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.MainNavTarget.ConverterTabbedNavTarget
import com.example.mvi_clean_demo.viewmodels.MainViewModel
import kotlinx.serialization.Serializable

sealed interface MainNavTarget {
    @Serializable
    data object ConverterTabbedNavTarget: MainNavTarget
    @Serializable
    data object BlogHostNavTarget: MainNavTarget
}

@Composable
fun ComposeMainNavHost(
    mainModel: MainViewModel.Model,
    sendEvent: (MainViewModel.Event) -> Unit,
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
                    navController.navigate(BlogHostNavTarget)
                }
            )
        }

        composable<BlogHostNavTarget> { backStackEntry ->
            ComposeBlogScreen(
                sendEvent = sendEvent,
                navigationTitle = mainModel.navigationTitle,
                onNavigateBack = onNavigateBack
            )
        }
    }
}