package com.example.mvi_clean_demo.common.ui_components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay

@Composable
fun navigationCompletionSafe(): State<Boolean> {
    val isNavigationAnimationCompleted = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(NavigationHelper.ANIMATION_DURATION.toLong())
        isNavigationAnimationCompleted.value = true
    }
    return isNavigationAnimationCompleted
}

object NavigationHelper {
    const val ANIMATION_DURATION: Int = 300
}