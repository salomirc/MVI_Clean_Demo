package com.example.mvi_clean_demo.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.repositories.Repository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@HiltViewModel(assistedFactory = TemperatureViewModel.Factory::class)
class TemperatureViewModel @AssistedInject constructor(
    @Assisted private val initialTempValue: String,
    private val repository: Repository,
) : BaseViewModel<TemperatureViewModel.Model, TemperatureViewModel.Event>() {

    @AssistedFactory
    interface Factory {
        fun create(initialTempValue: String): TemperatureViewModel
    }
    private var calculationJob: Job? = null

    data class Model(
        val temperature: String,
        val scale: Int,
        val isButtonEnabled: Boolean,
        val convertedValue: Float? = null
    )

    sealed interface Event {
        data class SetTemperature(val temperature: String): Event
        data class SetScale(val scale: Int): Event
        data class ValidateButtonEnabled(val temperature: String): Event
        data class Convert(val temperature: String, val scale: Int): Event
    }

    private val model = Model(
        temperature = repository.getString("temperature", initialTempValue),
        scale = repository.getInt("scale", R.string.celsius),
        isButtonEnabled = false
    )
    override val modelStateFlow = MutableStateFlow(model)

    override fun sendEvent(event: Event) {
        when (event) {
            is Event.SetTemperature -> {
                setTemperature(event.temperature)
            }
            is Event.SetScale -> {
                setScale(event.scale)
            }
            is Event.Convert -> {
                convert(temperature = event.temperature, scale = event.scale)
            }
            is Event.ValidateButtonEnabled -> {
                isButtonEnabled(event.temperature)
            }
        }
    }

    private fun isButtonEnabled(temperature: String) {
        val isEnabled = getTemperatureAsFloat(temperature) != null
        modelStateFlow.update { it.copy(isButtonEnabled = isEnabled) }
    }

    private fun convert(temperature: String, scale: Int) {
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            val calculationResult = withContext(Dispatchers.Default) {
                getTemperatureAsFloat(temperature)?.let {
                    if (scale == R.string.celsius) (it * 1.8F) + 32F else (it - 32F) / 1.8F
                }
            }
            modelStateFlow.update { it.copy(convertedValue = calculationResult) }
        }
    }

    private fun setTemperature(value: String) {
        modelStateFlow.update { it.copy(temperature = value) }
        repository.putString("temperature", value)
    }

    private fun setScale(value: Int) {
        modelStateFlow.update { it.copy(scale = value) }
        repository.putInt("scale", value)
    }

    private fun getTemperatureAsFloat(temperature: String): Float? = temperature.toFloatOrNull()
}
