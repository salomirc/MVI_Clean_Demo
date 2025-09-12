package com.example.mvi_clean_demo.sections.blog.presentation.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.PIXEL
import androidx.compose.ui.tooling.preview.Devices.PIXEL_3A
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntryModel
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.PostSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun PostChatBubble(
    postEntryModel: PostEntryModel,
    modifier: Modifier = Modifier,
    isUserMe: Boolean = true,
) {
    val bubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.tertiaryContainer
    }
    val alignment = if (isUserMe) Arrangement.End else Arrangement.Start
    val clipCornerRadius = 8.dp
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
            BubbleTriangle(
                isUserMe = false,
                boxCutCornerSize = boxCutCornerSize,
                bubbleColor = bubbleColor
            )
        }
        Box(
            modifier = Modifier
                .weight(1.0f),
            contentAlignment = if (isUserMe) Alignment.TopEnd else Alignment.TopStart
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = bubbleColor,
                        shape = RoundedCornerShape(
                            topStart = if (isUserMe) clipCornerRadius else 0.dp,
                            topEnd = if (isUserMe) 0.dp else clipCornerRadius,
                            bottomStart = clipCornerRadius,
                            bottomEnd = clipCornerRadius
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "userId: ${postEntryModel.userId}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "id: ${postEntryModel.id}",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = postEntryModel.body,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        if (isUserMe) {
            BubbleTriangle(
                isUserMe = true,
                boxCutCornerSize = boxCutCornerSize,
                bubbleColor = bubbleColor
            )
        }
    }
}

@Composable
private fun BubbleTriangle(
    isUserMe: Boolean,
    boxCutCornerSize: Dp,
    bubbleColor: Color
) {
    Box(
        modifier = Modifier
            .width(boxCutCornerSize)
            .height(boxCutCornerSize)
            .background(
                color = bubbleColor,
                shape = CutCornerShape(
                    topStart = 0.dp,
                    topEnd = 0.dp,
                    bottomStart = if (isUserMe) 0.dp else boxCutCornerSize,
                    bottomEnd = if (isUserMe) boxCutCornerSize else 0.dp
                )
            )
    )
}

@Preview(
    name = "Light Mode",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = PIXEL
)
@Preview(
    name = "Dark Mode",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = PIXEL
)
@Composable
fun PostChatBubblePreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                PostChatBubble(
                    postEntryModel = PostSampleData.models.first()
                )
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    group = "List",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = PIXEL_3A
)
@Preview(
    name = "Dark Mode",
    group = "List",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = PIXEL_3A
)
@Composable
fun PostChatBubblesPreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                PostSampleData.models.forEachIndexed { index, model ->
                    PostChatBubble(
                        postEntryModel = model,
                        isUserMe = index % 2 == 0
                    )
                }
            }
        }
    }
}