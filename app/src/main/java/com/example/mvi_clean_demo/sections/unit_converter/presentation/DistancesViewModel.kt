package com.example.mvi_clean_demo.sections.unit_converter.presentation

import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.base.BaseViewModel
import com.example.mvi_clean_demo.repositories.IDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class DistancesViewModel @Inject constructor(
    private val dataRepository: IDataRepository
) : BaseViewModel<DistancesViewModel.Model, DistancesViewModel.Event>(
    model = Model(
        isLoading = true,
        distance = "",
        unit = R.string.km
    )
) {

    data class Model(
        val isLoading: Boolean,
        val distance: String,
        val unit: Int,
        val convertedValue: Float? = null
    )

    sealed interface Event {
        data object GetData: Event
        data class SetDistance(val distance: String): Event
        data class SetUnit(val unit: Int): Event
        data class Convert(val distance: String, val unit: Int): Event
    }

    override suspend fun processEvent(event: Event) {
        when (event) {
            is Event.GetData -> {
                getData()
            }
            is Event.SetDistance -> {
                setDistance(event.distance)
            }
            is Event.SetUnit -> {
                setUnit(event.unit)
            }
            is Event.Convert -> {
                convert(distance = event.distance, event.unit)
            }
        }
    }

    private suspend fun getData() {
        updateModelStateSuspend { model ->
            withContext(Dispatchers.IO) {
                val distance = async { dataRepository.getString("distance", "") }
                val unit = async { dataRepository.getInt("unit", R.string.km) }
                val convertedValue = calculateConversion(distance = distance.await(), unit = unit.await())
                model.copy(
                    isLoading = false,
                    distance = distance.await(),
                    unit = unit.await(),
                    convertedValue = convertedValue
                )
            }
        }
    }

    private suspend fun convert(distance: String, unit: Int) {
        // move heavy computation on background thread or at least non blocking operation
        // on the main thread to avoid UI recomposition performance issues
        updateModelStateSuspend { model ->
            model.copy(
                convertedValue = withContext(Dispatchers.Default) {
                    calculateConversion(distance, unit)
                }
            )
        }
    }

    private fun calculateConversion(distance: String, unit: Int): Float? =
        getDistanceAsFloat(distance)?.let {
            if (unit == R.string.km) (it / 0.62137F) else (it * 0.62137F)
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
