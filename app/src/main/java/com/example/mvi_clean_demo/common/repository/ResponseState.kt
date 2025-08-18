package com.example.mvi_clean_demo.common.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

sealed interface ResponseState<out T> {
    data object Idle: ResponseState<Nothing>
    sealed interface ActiveResponseState<out T>: ResponseState<T> {
        data object Loading: ActiveResponseState<Nothing>
        data class Success<T>(val data: T): ActiveResponseState<T>
        data class Failure(val throwable: Throwable): ActiveResponseState<Nothing>
    }
}

fun <T> Result<T>.toFlow(): Flow<ActiveResponseState<T>> {
    val result = this
    return flow {
        result
            .onSuccess {
                emit(ActiveResponseState.Success(it))
            }
            .onFailure { throwable ->
                emit(ActiveResponseState.Failure(throwable))
            }
    }.onStart {
        emit(ActiveResponseState.Loading)
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