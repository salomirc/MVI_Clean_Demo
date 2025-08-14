package com.example.mvi_clean_demo.common.ui_components.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvi_clean_demo.common.ui_components.blog.ComposeBlogScreen
import com.example.mvi_clean_demo.common.ui_components.main.MainNavTarget.BlogHostNavTarget
import com.example.mvi_clean_demo.common.ui_components.main.MainNavTarget.ConverterTabbedNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterTabbedScreen
import com.example.mvi_clean_demo.MainViewModel
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