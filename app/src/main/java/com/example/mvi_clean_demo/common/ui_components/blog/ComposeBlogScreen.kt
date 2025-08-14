package com.example.mvi_clean_demo.common.ui_components.blog

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.mvi_clean_demo.common.ui_components.unit_converter.ComposeUnitConverterTopBar
import com.example.mvi_clean_demo.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun ComposeBlogScreen(
    sendEvent: (MainViewModel.Event) -> Unit,
    navigationTitle: String,
    onNavigateBack: () -> Unit
) {
    ComposeBlogScaffold(
        navigationTitle = navigationTitle,
        onNavigateBack = onNavigateBack,
        content = { innerPadding ->
            ComposeBlogNavHost(
                sendEvent = sendEvent,
                modifier = Modifier.padding(innerPadding),
            )
        }
    )
}

@Composable
fun ComposeBlogScaffold(
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