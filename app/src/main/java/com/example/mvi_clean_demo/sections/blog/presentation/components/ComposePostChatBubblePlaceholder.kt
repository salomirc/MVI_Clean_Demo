package com.example.mvi_clean_demo.sections.blog.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.PIXEL
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.common.ui_components.ShimmerPlaceholder
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun PostChatBubblePlaceholder(
    modifier: Modifier = Modifier,
    isUserMe: Boolean = true,
) {
    val alignment = if (isUserMe) Arrangement.End else Arrangement.Start
    val boxCutCornerSize = 8.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (isUserMe) 32.dp else 8.dp,
                end = if (isUserMe) 8.dp else 32.dp,
                bottom = 16.dp
            ),
        horizontalArrangement = alignment
    ) {
        if (!isUserMe) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .width(boxCutCornerSize)
                    .height(boxCutCornerSize)
            )
        }
        Box(
            modifier = Modifier
                .weight(1.0f),
            contentAlignment = if (isUserMe) Alignment.TopEnd else Alignment.TopStart
        ) {
            Column {
                ShimmerPlaceholder(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }
        }
        if (isUserMe) {
            ShimmerPlaceholder(
                modifier = Modifier
                    .width(boxCutCornerSize)
                    .height(boxCutCornerSize)
            )
        }
    }
}


@Preview(
    name = "Light Mode Placeholder List",
    group = "List",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = PIXEL_3A
)
@Preview(
    name = "Dark Mode Placeholder List",
    group = "List",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = PIXEL_3A
)
@Composable
fun PostChatBubblesPlaceholderPreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                repeat(5) { index ->
                    PostChatBubblePlaceholder(
                        isUserMe = index % 2 == 0
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Light Mode Placeholder",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = PIXEL
)
@Preview(
    name = "Dark Mode Placeholder",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = PIXEL
)
@Composable
fun PostChatBubblePlaceholderPreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                PostChatBubblePlaceholder()
            }
        }
    }
}