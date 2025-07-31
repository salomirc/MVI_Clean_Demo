package com.example.mvi_clean_demo.viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow


interface MVI<M, E> {
    val modelStateFlow: StateFlow<M>
    fun sendEvent(event: E)
}

abstract class BaseViewModel<M, E>: ViewModel(), MVI<M, E>