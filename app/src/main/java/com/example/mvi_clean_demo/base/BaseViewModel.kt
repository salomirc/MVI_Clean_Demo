package com.example.mvi_clean_demo.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


interface MVI<Model, Event> {
    val modelStateFlow: StateFlow<Model>
    fun sendEvent(event: Event)
    suspend fun processEvent(event: Event)
}

abstract class BaseViewModel<Model, Event>(model: Model): ViewModel(), MVI<Model, Event> {
    private val _modelStateFlow: MutableStateFlow<Model> = MutableStateFlow(model)
    override val modelStateFlow: StateFlow<Model> = _modelStateFlow

    override fun sendEvent(event: Event) {
        viewModelScope.launch {
            processEvent(event)
        }
    }

    fun updateModelState(function: (Model) -> Model) {
        _modelStateFlow.update(function)
    }

    suspend fun updateModelStateSuspend(function: suspend (Model) -> Model) {
        _modelStateFlow.update { model ->
            function(model)
        }
    }
}

abstract class BaseViewModelRepeatOnStart<Model, Event>(
    model: Model,
    repeatOnStartCollectingModelStateFlow: (BaseViewModel<Model, Event>) -> Unit
): BaseViewModel<Model, Event>(model) {

    override val modelStateFlow: StateFlow<Model> =
        super.modelStateFlow
            .onStart {
                repeatOnStartCollectingModelStateFlow(this@BaseViewModelRepeatOnStart)
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000L),
                initialValue = super.modelStateFlow.value
            )
}