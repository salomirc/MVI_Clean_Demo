package com.example.mvi_clean_demo.common.ui_components.unit_converter

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mvi_clean_demo.viewmodels.MainViewModel

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