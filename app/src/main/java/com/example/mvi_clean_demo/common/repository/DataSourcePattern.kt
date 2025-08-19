package com.example.mvi_clean_demo.common.repository

import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow

object DataSourcePattern {
    fun <T> dualPattern(
        networkResult: suspend FlowCollector<ActiveResponseState<T>>.() -> Result<T>,
        localResult: suspend FlowCollector<ActiveResponseState<T>>.() -> Result<T>
    ): Flow<ActiveResponseState<T>> = flow {
        emit(ActiveResponseState.Loading)
        networkResult()
            .onSuccess {
                emit(ActiveResponseState.Success(it))
            }
            .onFailure { throwable ->
                emit(ActiveResponseState.Failure(throwable))
                emitResult(localResult())
            }
    }

    fun <T> singlePattern(
        result: suspend () -> Result<T>
    ): Flow<ActiveResponseState<T>> = flow {
        emitResult(result())
    }

    private suspend fun <T> FlowCollector<ActiveResponseState<T>>.emitResult(
        result: Result<T>
    ) {
        emit(ActiveResponseState.Loading)
        result
            .onSuccess {
                emit(ActiveResponseState.Success(it))
            }
            .onFailure { throwable ->
                emit(ActiveResponseState.Failure(throwable))
            }
    }
}