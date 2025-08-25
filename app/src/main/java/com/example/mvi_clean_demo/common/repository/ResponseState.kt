package com.example.mvi_clean_demo.common.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Loading
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle

sealed interface ResponseState<out T> {
    object Idle: ResponseState<Nothing>
    sealed interface ActiveResponseState<out T>: ResponseState<T> {
        object Loading: ActiveResponseState<Nothing>
        class Success<T>(val data: T): ActiveResponseState<T>
        class Failure(val throwable: Throwable): ActiveResponseState<Nothing>
    }
}

inline fun <T, R> ActiveResponseState<T>.activeResponseStateWrapper(
    crossinline transform: (value: T) -> R
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
        Loading -> {
            result = Loading
        }
    }
    return result
}

inline fun <T, R> ResponseState<T>.responseStateWrapper(
    crossinline transform: (value: T) -> R
): ResponseState<R> {
    lateinit var result: ResponseState<R>
    when (this) {
        is ActiveResponseState -> {
            result = this.activeResponseStateWrapper(transform)
        }
        Idle -> Idle
    }
    return result
}