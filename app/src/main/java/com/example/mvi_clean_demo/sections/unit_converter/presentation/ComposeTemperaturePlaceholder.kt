package com.example.mvi_clean_demo.sections.unit_converter.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.common.ui_components.ShimmerPlaceholder

@Composable
fun TemperaturePlaceholder(hasButton: Boolean = false) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Input field placeholder
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .padding(start = 40.dp)
                    .fillMaxWidth(0.8f)
                    .height(56.dp)
            )
            ShimmerPlaceholder(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .width(32.dp)
                    .height(20.dp)
            )
        }

        // Scale selector placeholder
        ShimmerPlaceholder(
            modifier = Modifier.size(width = 200.dp, height = 40.dp),
            cornerRadius = 20
        )

        // Result placeholder
        ShimmerPlaceholder(
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .height(28.dp)
        )
        if (hasButton) {
            Spacer(
                modifier = Modifier
                    .width(10.dp)
                    .weight(1f)
            )
            ShimmerPlaceholder(
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .size(width = 128.dp, height = 40.dp),
                cornerRadius = 20
            )
        }
    }
}