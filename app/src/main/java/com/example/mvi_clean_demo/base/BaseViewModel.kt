package com.example.mvi_clean_demo.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update


interface MVI<Model, Event> {
    val modelStateFlow: StateFlow<Model>
    fun sendEvent(event: Event)
}

abstract class BaseViewModel<Model, Event>(model: Model): ViewModel(), MVI<Model, Event> {
    private val _modelStateFlow: MutableStateFlow<Model> = MutableStateFlow(model)
    override val modelStateFlow: StateFlow<Model>
        get() = _modelStateFlow

    protected fun updateModelState(function: (Model) -> Model) {
        _modelStateFlow.update(function)
    }

    protected suspend fun updateModelStateSuspend(function: suspend (Model) -> Model) {
        _modelStateFlow.update { model ->
            function(model)
        }
    }
}