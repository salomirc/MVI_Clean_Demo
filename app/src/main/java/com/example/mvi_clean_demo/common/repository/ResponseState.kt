package com.example.mvi_clean_demo.common.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success

sealed interface ResponseState<out T> {
    data object Idle: ResponseState<Nothing>
    sealed interface ActiveResponseState<out T>: ResponseState<T> {
        data object Loading: ActiveResponseState<Nothing>
        data class Success<T>(val data: T): ActiveResponseState<T>
        data class Failure(val throwable: Throwable): ActiveResponseState<Nothing>
    }
}

suspend inline fun <T, R> ActiveResponseState<T>.activeResponseStateWrapper(
    crossinline transform: suspend (value: T) -> R
): ActiveResponseState<R> {
    lateinit var result: ActiveResponseState<R>
    when (this) {
        is Success -> {
            this.runCatching {
                transform(this.data)
            }
                .onSuccess { data ->
                    result = Success(data)
                }
                .onFailure { throwable ->
                    result = Failure(throwable)
                }
        }
        is Failure -> {
            result = Failure(this.throwable)
        }
        ActiveResponseState.Loading -> {
            result = ActiveResponseState.Loading
        }
    }
    return result
}