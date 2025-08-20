package com.example.mvi_clean_demo

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mvi_clean_demo.common.error_handling.ErrorAction
import com.example.mvi_clean_demo.common.ui_components.main.ComposeMainNavHost
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeUnitConverterTheme {
                val viewModel: MainViewModel = hiltViewModel()
                val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
                ComposeUnitConverterWrapper(
                    model = model,
                    sendEvent = { event ->
                        viewModel.sendEvent(event)
                    },
                    onNavigateBack = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}

@Composable
fun ComposeUnitConverterWrapper(
    model: MainViewModel.Model,
    sendEvent: (MainViewModel.Event) -> Unit,
    onNavigateBack: () -> Unit
) {
    ComposeMainNavHost(
        mainModel = model,
        sendEvent = sendEvent,
        onNavigateBack = onNavigateBack
    )
    GlobalActionAndMessageToastSetUp(
        model = model,
        sendEvent = sendEvent
    )
}

@Composable
private fun GlobalActionAndMessageToastSetUp(
    model: MainViewModel.Model,
    sendEvent: (MainViewModel.Event) -> Unit
) {
    val context = LocalContext.current
    LaunchedEffect(model.messageResourceIdWrapper) {
        model.messageResourceIdWrapper?.let { wrapper ->
            Log.d(
                "ToastMessage",
                "MainActivity toast LaunchedEffect have been called with: ${model.messageResourceIdWrapper}"
            )
            val message = context.getString(wrapper.stringResId)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            Log.d("ToastMessage", "MainActivity Toast have been called with: $message")

            wrapper.errorAction?.let { errorAction ->
                when (errorAction) {
                    ErrorAction.LOG_OUT -> {
                        sendEvent(MainViewModel.Event.LogOut)
                    }
                }
            }
        }
    }
}