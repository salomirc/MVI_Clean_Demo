package com.example.mvi_clean_demo.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

fun ColorScheme.getDisabledColor(isDarkTheme: Boolean): Color =
    if (isDarkTheme) Gray70 else Gray30