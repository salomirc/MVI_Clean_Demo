package com.example.mvi_clean_demo.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


interface MVI<Model, Event, Effect> {
    val modelStateFlow: StateFlow<Model>
    val effectFlow: Flow<Effect>
    fun sendEvent(event: Event)
    suspend fun processEvent(event: Event)
}

abstract class BaseViewModel<Model, Event, Effect>(model: Model): ViewModel(), MVI<Model, Event, Effect> {
    private val _modelStateFlow: MutableStateFlow<Model> = MutableStateFlow(model)
    override val modelStateFlow: StateFlow<Model> = _modelStateFlow

    private val _effect = Channel<Effect>()
    override val effectFlow: Flow<Effect> = _effect.receiveAsFlow()

    override fun sendEvent(event: Event) {
        viewModelScope.launch {
            processEvent(event)
        }
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
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

abstract class BaseViewModelRepeatOnStart<Model, Event, Effect>(
    model: Model,
    repeatOnStartCollectingModelStateFlow: (BaseViewModel<Model, Event, Effect>) -> Unit
): BaseViewModel<Model, Event, Effect>(model) {

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