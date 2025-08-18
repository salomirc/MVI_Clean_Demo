package com.example.mvi_clean_demo.common.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

object DataSourcePattern {
    fun <T> dualPattern(
        localResult: suspend () -> Result<T>,
        networkResult: suspend () -> Result<T>
    ): Flow<ActiveResponseState<T>> = flow {
        emit(ActiveResponseState.Loading)
        localResult()
            .onSuccess {
                emit(ActiveResponseState.Success(it))
            }
            .onFailure { throwable ->
                emit(ActiveResponseState.Failure(throwable))
            }
        emit(ActiveResponseState.Loading)
        networkResult()
            .onSuccess {
                emit(ActiveResponseState.Success(it))
            }
            .onFailure { throwable ->
                emit(ActiveResponseState.Failure(throwable))
            }
    }

    fun <T> singlePattern(
        result: suspend () -> Result<T>
    ): Flow<ActiveResponseState<T>> = flow {
        emit(ActiveResponseState.Loading)
        result()
            .onSuccess {
                emit(ActiveResponseState.Success(it))
            }
            .onFailure { throwable ->
                emit(ActiveResponseState.Failure(throwable))
            }
    }
}