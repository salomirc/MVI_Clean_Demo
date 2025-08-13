package com.example.mvi_clean_demo.viewmodels

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.common.error_handling.IErrorHandlerBroadcastService
import com.example.mvi_clean_demo.common.error_handling.MessageResourceIdWrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val broadcastService: IErrorHandlerBroadcastService
) : BaseViewModel<MainViewModel.Model, MainViewModel.Event>(
    model = Model(
        messageResourceIdWrapper = null,
        navigationTitle = "",
        shouldDisplayBottomBar = true
    )
) {
    init {
        collectMessageResourceIdWrapper()
        startProcessNextMessageLoop()
    }
    private fun collectMessageResourceIdWrapper() {
        viewModelScope.launch {
            broadcastService.messageResourceIdWrapper.collect { messageResourceIdWrapper ->
                updateModelState { model ->
                    model.copy(messageResourceIdWrapper = messageResourceIdWrapper)
                }
                Log.d("ToastMessage", "messageResourceIdWrapper: $messageResourceIdWrapper")
            }
        }
    }
    private fun startProcessNextMessageLoop() {
        viewModelScope.launch {
            while (true) {
                broadcastService.processNextMessage()
                Log.d("ToastMessage", "MainViewModel processNextMessage() called")
                delay(3000L)
            }
        }
    }

    data class Model(
        val messageResourceIdWrapper: MessageResourceIdWrapper?,
        val navigationTitle: String,
        val shouldDisplayBottomBar: Boolean
    )

    sealed interface Event {
        data class SetNavigationTitle(val title: String) : Event
        data class SetShouldDisplayBottomBar(val isVisible: Boolean) : Event
    }

    override fun sendEvent(event: Event) {
        when (event) {
            is Event.SetNavigationTitle -> {
                setNavigationTitle(event.title)
            }
            is Event.SetShouldDisplayBottomBar -> {
                setShouldDisplayBottomBar(event.isVisible)
            }
        }
    }

    private fun setShouldDisplayBottomBar(visible: Boolean) {
        updateModelState { model ->
            model.copy(shouldDisplayBottomBar = visible)
        }
    }

    private fun setNavigationTitle(title: String) {
        updateModelState { model ->
            model.copy(navigationTitle = title)
        }
    }
}