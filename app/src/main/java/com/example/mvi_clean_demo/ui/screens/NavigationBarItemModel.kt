package com.example.mvi_clean_demo.ui.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mvi_clean_demo.R
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data class TemperatureDestination(val initialTempValue: String): Destination
    @Serializable
    data object DistancesDestination : Destination
}

sealed interface NavigationItemModel {
    val destination: Destination
    @get:StringRes
    val label: Int
    val icon: ImageVector


    data object Temperature : NavigationItemModel {
        override val destination = Destination.TemperatureDestination(initialTempValue = "300")
        override val label = R.string.temperature
        override val icon = Icons.Default.Thermostat
    }

    data object Distance : NavigationItemModel {
        override val destination = Destination.DistancesDestination
        override val label = R.string.distances
        override val icon = Icons.Default.SquareFoot
    }
}
