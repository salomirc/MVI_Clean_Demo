package com.example.mvi_clean_demo.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@get:Composable
val ColorScheme.disabled: Color
    get() {
        return if (isSystemInDarkTheme()) Gray70 else Gray30
    }