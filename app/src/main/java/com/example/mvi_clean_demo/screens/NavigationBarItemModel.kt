package com.example.mvi_clean_demo.screens

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.common.ui_components.NavTarget

sealed interface NavigationItemModel {
    val navTarget: NavTarget
    @get:StringRes
    val label: Int
    val icon: ImageVector

    data object Temperature : NavigationItemModel {
        override val navTarget = NavTarget.TemperatureNavTarget(initialTempValue = "300")
        override val label = R.string.temperature
        override val icon = Icons.Default.Thermostat
    }

    data object Distance : NavigationItemModel {
        override val navTarget = NavTarget.DistancesNavTarget
        override val label = R.string.distances
        override val icon = Icons.Default.SquareFoot
    }
}
