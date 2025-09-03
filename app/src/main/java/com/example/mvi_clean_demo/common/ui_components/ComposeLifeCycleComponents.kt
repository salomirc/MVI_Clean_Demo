package com.example.mvi_clean_demo.common.ui_components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.INITIALIZED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope

@Composable
fun ComposeLifecycleEvent(
    onCreate: () -> Unit = {},
    onStart: () -> Unit = {},
    onResume: () -> Unit = {},
    onPause: () -> Unit = {},
    onStop: () -> Unit = {},
    onDestroy: () -> Unit = {}
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Event.ON_CREATE -> onCreate()
                Event.ON_START -> onStart()
                Event.ON_RESUME -> onResume()
                Event.ON_PAUSE -> onPause()
                Event.ON_STOP -> onStop()
                Event.ON_DESTROY -> onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun ComposeLifecycleState(
    onStateInitialized: () -> Unit = {},
    onStateCreated: () -> Unit = {},
    onStateStarted: () -> Unit = {},
    onStateResumed: () -> Unit = {},
    onStateDestroyed: () -> Unit = {}
) {
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    LaunchedEffect(lifecycleState) {
        when (lifecycleState) {
            INITIALIZED -> onStateInitialized()
            CREATED -> onStateCreated()
            STARTED -> onStateStarted()
            RESUMED -> onStateResumed()
            DESTROYED -> onStateDestroyed()
        }
    }
}

@Composable
fun ComposeRepeatOnLifecycle(
    state: Lifecycle.State,
    block: suspend CoroutineScope.(Lifecycle.State) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state) {
            val currentState = lifecycleOwner.lifecycle.currentState
            block(currentState)
        }
    }
}

@Composable
fun LogComposeLifecycleEvent(composableName: String) {
    ComposeLifecycleEvent(
        onCreate = {
            Log.d("ComposeLifecycleObserver", "$composableName onCreate called")
        },
        onStart = {
            Log.d("ComposeLifecycleObserver", "$composableName onStart called")
        },
        onResume = {
            Log.d("ComposeLifecycleObserver", "$composableName onResume called")
        },
        onPause = {
            Log.d("ComposeLifecycleObserver", "$composableName onPause called")
        },
        onStop = {
            Log.d("ComposeLifecycleObserver", "$composableName onStop called")
        },
        onDestroy = {
            Log.d("ComposeLifecycleObserver", "$composableName onDestroy called")
        }
    )
}