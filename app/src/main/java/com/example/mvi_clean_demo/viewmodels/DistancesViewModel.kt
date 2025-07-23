package com.example.mvi_clean_demo.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DistancesViewModel(private val repository: Repository) : ViewModel() {

    private val _unit: MutableStateFlow<Int> = MutableStateFlow(
        repository.getInt("unit", R.string.meter)
    )

    val unit: StateFlow<Int>
        get() = _unit

    fun setUnit(value: Int) {
        _unit.value = value
        repository.putInt("unit", value)
    }

    private val _distance: MutableStateFlow<String> = MutableStateFlow(
        repository.getString("distance", "")
    )

    val distance: StateFlow<String>
        get() = _distance

    fun setDistance(value: String) {
        _distance.value = value
        repository.putString("distance", value)
    }

    private fun getDistanceAsFloat(s: String): Float {
        return try {
            s.toFloat()
        } catch (e: NumberFormatException) {
            Float.NaN
        }
    }

    fun isButtonEnabled(s: String): Boolean = !getDistanceAsFloat(s).isNaN()

    private val _convertedDistance: MutableStateFlow<Float> = MutableStateFlow(Float.NaN)

    val convertedDistance: StateFlow<Float>
        get() = _convertedDistance

    fun convert(s: String) {
        getDistanceAsFloat(s).let {
            viewModelScope.launch {
//                delay(5000)
                _convertedDistance.value = if (!it.isNaN())
                    if (_unit.value == R.string.meter)
                        it * 0.00062137F
                    else
                        it / 0.00062137F
                else
                    Float.NaN
            }
        }
    }
}
