package com.example.mvi_clean_demo.sections.unit_converter.presentation

import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.base.BaseViewModel
import com.example.mvi_clean_demo.repositories.IDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@HiltViewModel(assistedFactory = TemperatureViewModel.Factory::class)
class TemperatureViewModel @AssistedInject constructor(
    @Assisted private val initialTempValue: String,
    private val dataRepository: IDataRepository
) : BaseViewModel<TemperatureViewModel.Model, TemperatureViewModel.Event>(
    model = Model(
        temperature = "",
        scale = R.string.celsius,
        isButtonEnabled = false
    )
) {

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            val temperature = dataRepository.getString("temperature", initialTempValue)
            val scale = dataRepository.getInt("scale", R.string.celsius)
            updateModelState { model ->
                model.copy(
                    temperature = temperature,
                    scale = scale
                )
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(initialTempValue: String): TemperatureViewModel
    }

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

    override suspend fun processEvent(event: Event) {
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
        updateModelState { model -> model.copy(isButtonEnabled = isEnabled) }
    }

    private suspend fun convert(temperature: String, scale: Int) {
        // move heavy computation on background thread or at least non blocking operation
        // on the main thread to avoid UI recomposition performance issues
        val calculationResult = withContext(Dispatchers.Default) {
            getTemperatureAsFloat(temperature)?.let {
                if (scale == R.string.celsius) (it * 1.8F) + 32F else (it - 32F) / 1.8F
            }
        }
        updateModelState { model -> model.copy(convertedValue = calculationResult) }
    }

    private fun setTemperature(value: String) {
        updateModelState { model -> model.copy(temperature = value) }
        dataRepository.putString("temperature", value)
    }

    private fun setScale(value: Int) {
        updateModelState { model -> model.copy(scale = value) }
        dataRepository.putInt("scale", value)
    }

    private fun getTemperatureAsFloat(temperature: String): Float? = temperature.toFloatOrNull()
}
