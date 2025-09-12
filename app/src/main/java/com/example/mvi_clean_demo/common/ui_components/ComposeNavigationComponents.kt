package com.example.mvi_clean_demo.common.ui_components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay

@Composable
fun navigationCompletionSafe(
    navController: NavHostController,
    animationDuration: Int,
    navigationTarget: String?
): State<Boolean> {
    val isNavigationAnimationCompleted = remember { mutableStateOf(false) }
    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { entry ->
            val route = entry.destination.route ?: return@collect
            when {
                navigationTarget?.let { route.contains(it) } == true -> {
                    // Delay until enterTransition finishes
                    delay(animationDuration.toLong())
                    isNavigationAnimationCompleted.value = true
                }
            }
        }
    }
    return isNavigationAnimationCompleted
}