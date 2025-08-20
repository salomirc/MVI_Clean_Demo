package com.example.mvi_clean_demo.common.error_handling

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.LinkedList
import javax.inject.Inject
import javax.inject.Singleton

interface IErrorHandlerBroadcastService {
    val messageResourceIdWrapper: StateFlow<MessageResourceIdWrapper?>
    fun setErrorMessage(@StringRes stringResInt: Int, errorAction: ErrorAction? = null)
    fun processNextMessage()
}

@Singleton
class ErrorHandlerBroadcastService @Inject constructor() : IErrorHandlerBroadcastService {
    private val queue = LinkedList<MessageResourceIdWrapper>()
    private val _messageResourceIdWrapper = MutableStateFlow<MessageResourceIdWrapper?>(null)
    override val messageResourceIdWrapper: StateFlow<MessageResourceIdWrapper?>
        get() = _messageResourceIdWrapper

    override fun setErrorMessage(@StringRes stringResInt: Int, errorAction: ErrorAction?) {
        queue.addLast(
            MessageResourceIdWrapper(
                stringResId = stringResInt,
                errorAction = errorAction
            )
        )
    }

    override fun processNextMessage() {
        if (queue.isNotEmpty()) {
            val peekValue = queue.peek()
            _messageResourceIdWrapper.value = peekValue
            queue.removeFirst()
        } else {
            _messageResourceIdWrapper.value = null
        }
    }
}

// An instance class used to achieve reference comparison of the MutableStateFlow setter new value
class MessageResourceIdWrapper(
    val stringResId: Int,
    val errorAction: ErrorAction? = null
)

enum class ErrorAction {
    LOG_OUT;
}