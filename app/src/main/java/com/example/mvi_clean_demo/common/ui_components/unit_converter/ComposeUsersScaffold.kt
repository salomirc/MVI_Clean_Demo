package com.example.mvi_clean_demo.common.ui_components.unit_converter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import kotlinx.coroutines.launch

@Composable
fun ComposeUsersScaffold(
    navigationTitle: String,
    onNavigateBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val textMenuItems = listOf("Item #1", "Item #2")
    val snackBarCoroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ComposeUnitConverterTopBar(
                textMenuItems = textMenuItems,
                navTitle = navigationTitle,
                onBack = onNavigateBack,
                onClick = { s ->
                    snackBarCoroutineScope.launch {
                        snackBarHostState.showSnackbar(message = s)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = content
    )
}