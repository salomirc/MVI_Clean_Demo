package com.example.mvi_clean_demo.sections.unit_converter.presentation

import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.base.BaseViewModel
import com.example.mvi_clean_demo.repositories.IDataRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext


@HiltViewModel(assistedFactory = TemperatureViewModel.Factory::class)
class TemperatureViewModel @AssistedInject constructor(
    @Assisted private val initialTempValue: String,
    private val dataRepository: IDataRepository
) : BaseViewModel<TemperatureViewModel.Model, TemperatureViewModel.Event>(
    model = Model(
        isLoading = true,
        temperature = "",
        scale = R.string.celsius
    )
) {
    @AssistedFactory
    interface Factory {
        fun create(initialTempValue: String): TemperatureViewModel
    }

    data class Model(
        val isLoading: Boolean,
        val temperature: String,
        val scale: Int,
        val convertedValue: Float? = null
    )

    sealed interface Event {
        data object GetData: Event
        data class SetTemperature(val temperature: String): Event
        data class SetScale(val scale: Int): Event
        data class Convert(val temperature: String, val scale: Int): Event
    }

    override suspend fun processEvent(event: Event) {
        when (event) {
            is Event.GetData -> {
                getData()
            }
            is Event.SetTemperature -> {
                setTemperature(event.temperature)
            }
            is Event.SetScale -> {
                setScale(event.scale)
            }
            is Event.Convert -> {
                convert(temperature = event.temperature, scale = event.scale)
            }
        }
    }
    private suspend fun getData() {
        updateModelStateSuspend { model ->
            withContext(Dispatchers.IO) {
                val temperature = async { dataRepository.getString("temperature", initialTempValue) }
                val scale = async { dataRepository.getInt("scale", R.string.celsius) }
                val convertedValue = calculateConversion(temperature = temperature.await(), scale = scale.await())
                model.copy(
                    isLoading = false,
                    temperature = temperature.await(),
                    scale = scale.await(),
                    convertedValue = convertedValue
                )
            }
        }
    }

    private suspend fun convert(temperature: String, scale: Int) {
        // move heavy computation on background thread or at least non blocking operation
        // on the main thread to avoid UI recomposition performance issues
        updateModelStateSuspend { model ->
            model.copy(
                convertedValue = withContext(Dispatchers.Default) {
                    calculateConversion(temperature, scale)
                }
            )
        }
    }

    private fun calculateConversion(temperature: String, scale: Int): Float? =
        getTemperatureAsFloat(temperature)?.let {
            if (scale == R.string.celsius) ((it - 32F) / 1.8F) else ((it * 1.8F) + 32F)
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
