package com.example.mvi_clean_demo.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface ITemperatureViewModel {
    val temperature: StateFlow<String>
    val scale: StateFlow<Int>
    val convertedValue: StateFlow<Float?>
    fun setScale(value: Int)
    fun setTemperature(value: String)
    fun isButtonEnabled(s: String): Boolean
    fun convert(s: String, scale: Int)
}

class TemperatureViewModel(
    initialTempValue: String,
    private val repository: Repository,
) : ViewModel(), ITemperatureViewModel {

    override val temperature = MutableStateFlow(repository.getString("temperature", initialTempValue))
    override val scale = MutableStateFlow(repository.getInt("scale", R.string.celsius))
    override val convertedValue = MutableStateFlow<Float?>(null)

    override fun setTemperature(value: String) {
        temperature.value = value
        repository.putString("temperature", value)
    }

    override fun setScale(value: Int) {
        scale.value = value
        repository.putInt("scale", value)
    }

    private fun getTemperatureAsFloat(s: String): Float? = s.toFloatOrNull()

    override fun isButtonEnabled(s: String): Boolean = getTemperatureAsFloat(s) != null

    override fun convert(s: String, scale: Int) {
        val calculationResult = getTemperatureAsFloat(s)?.let {
            if (scale == R.string.celsius) (it * 1.8F) + 32F else (it - 32F) / 1.8F
        }
        convertedValue.value = calculationResult
    }
}
