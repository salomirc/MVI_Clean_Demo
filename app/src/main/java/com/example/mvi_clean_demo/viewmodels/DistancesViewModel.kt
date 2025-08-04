package com.example.mvi_clean_demo.viewmodels

import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DistancesViewModel(
    private val repository: Repository
) : BaseViewModel<DistancesViewModel.Model, DistancesViewModel.Event>() {

    private var calculationJob: Job? = null

    data class Model(
        val distance: String,
        val unit: Int,
        val isButtonEnabled: Boolean,
        val convertedValue: Float? = null
    )

    sealed interface Event {
        data class SetDistance(val distance: String): Event
        data class SetUnit(val unit: Int): Event
        data class ValidateButtonEnabled(val distance: String): Event
        data class Convert(val distance: String, val unit: Int): Event
    }

    private val model = Model(
        distance = repository.getString("distance", ""),
        unit = repository.getInt("unit", R.string.meter),
        isButtonEnabled = false
    )
    override val modelStateFlow = MutableStateFlow(model)

    override fun sendEvent(event: Event) {
        when (event) {
            is Event.SetDistance -> {
                setDistance(event.distance)
            }
            is Event.SetUnit -> {
                setUnit(event.unit)
            }
            is Event.Convert -> {
                convert(distance = event.distance, event.unit)
            }
            is Event.ValidateButtonEnabled -> {
                isButtonEnabled(event.distance)
            }
        }
    }

    private fun isButtonEnabled(distance: String) {
        val isEnabled = getDistanceAsFloat(distance) != null
        modelStateFlow.update { it.copy(isButtonEnabled = isEnabled) }
    }

    private fun convert(distance: String, unit: Int) {
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            val calculationResult = withContext(Dispatchers.Default) {
                getDistanceAsFloat(distance)?.let {
                    if (unit == R.string.meter) (it * 0.00062137F) else (it / 0.00062137F)
                }
            }
            modelStateFlow.update { it.copy(convertedValue = calculationResult) }
        }
    }

    private fun setDistance(value: String) {
        modelStateFlow.update { it.copy(distance = value) }
        repository.putString("distance", value)
    }

    private fun setUnit(value: Int) {
        modelStateFlow.update { it.copy(unit = value) }
        repository.putInt("unit", value)
    }

    private fun getDistanceAsFloat(distance: String): Float? = distance.toFloatOrNull()
}
