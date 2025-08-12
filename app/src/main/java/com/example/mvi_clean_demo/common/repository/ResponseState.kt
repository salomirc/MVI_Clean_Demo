package com.example.mvi_clean_demo.common.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
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