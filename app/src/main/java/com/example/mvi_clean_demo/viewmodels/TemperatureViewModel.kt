package com.example.mvi_clean_demo.viewmodels

import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class TemperatureViewModel(
    initialTempValue: String,
    private val repository: Repository,
) : BaseViewModel<TemperatureViewModel.Model, TemperatureViewModel.Event>() {

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
        val calculationResult = getTemperatureAsFloat(temperature)?.let {
            if (scale == R.string.celsius) (it * 1.8F) + 32F else (it - 32F) / 1.8F
        }
        modelStateFlow.update { it.copy(convertedValue = calculationResult) }
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
