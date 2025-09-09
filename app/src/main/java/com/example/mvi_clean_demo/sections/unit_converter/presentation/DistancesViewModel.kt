package com.example.mvi_clean_demo.sections.unit_converter.presentation

import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.base.BaseViewModel
import com.example.mvi_clean_demo.repositories.IDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DistancesViewModel @Inject constructor(
    private val dataRepository: IDataRepository
) : BaseViewModel<DistancesViewModel.Model, DistancesViewModel.Event>(
    model = Model(
        distance = "",
        unit = R.string.km,
        isButtonEnabled = false
    )
) {

    init {
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            val distance = dataRepository.getString("distance", "")
            val unit = dataRepository.getInt("unit", R.string.km)
            updateModelState { model ->
                model.copy(
                    distance = distance,
                    unit = unit
                )
            }
        }
    }

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

    override suspend fun processEvent(event: Event) {
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
        updateModelState { model -> model.copy(isButtonEnabled = isEnabled) }
    }

    private suspend fun convert(distance: String, unit: Int) {
        // move heavy computation on background thread or at least non blocking operation
        // on the main thread to avoid UI recomposition performance issues
        val calculationResult = withContext(Dispatchers.Default) {
            getDistanceAsFloat(distance)?.let {
                if (unit == R.string.km) (it / 0.62137F) else (it * 0.62137F)
            }
        }
        updateModelState { model -> model.copy(convertedValue = calculationResult) }
    }

    private fun setDistance(value: String) {
        updateModelState { model -> model.copy(distance = value) }
        dataRepository.putString("distance", value)
    }

    private fun setUnit(value: Int) {
        updateModelState { model -> model.copy(unit = value) }
        dataRepository.putInt("unit", value)
    }

    private fun getDistanceAsFloat(distance: String): Float? = distance.toFloatOrNull()
}
