package com.example.mvi_clean_demo.common.error_handling

import android.util.Log
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

interface IErrorHandlerBroadcastService {
    val messageResourceIdWrapper: StateFlow<MessageResourceIdWrapper?>
    fun setErrorMessage(@StringRes stringResInt: Int)
    fun processNextMessage()
}

@Singleton
class ErrorHandlerBroadcastService @Inject constructor(): IErrorHandlerBroadcastService {
    private val queue = LinkedList<Int>()
    private val _messageResourceIdWrapper = MutableStateFlow<MessageResourceIdWrapper?>(null)
    override val messageResourceIdWrapper: StateFlow<MessageResourceIdWrapper?>
        get() = _messageResourceIdWrapper

    override fun setErrorMessage(@StringRes stringResInt: Int) {
        Log.d("ToastMessage","ErrorHandlerBroadcastService setErrorMessage() called with: $stringResInt")
        queue.addLast(stringResInt)
    }

    override fun processNextMessage() {
        if (queue.isNotEmpty()) {
            val peekValue = queue.peek()?.let { value -> MessageResourceIdWrapper(value) }
            Log.d("ToastMessage","ErrorHandlerBroadcastService _messageResourceIdWrapper.value() called with: $peekValue")
            _messageResourceIdWrapper.value = peekValue
            queue.removeFirst()
        } else {
            _messageResourceIdWrapper.value = null
        }
    }
}

// An instance class used to achieve reference comparison of the MutableStateFlow setter new value
class MessageResourceIdWrapper(
    val stringResId: Int
)