package com.example.mvi_clean_demo.sections.blog.presentation

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.common.ui_components.ComposeLifecycleEvent
import com.example.mvi_clean_demo.common.ui_components.LogComposeLifecycleEvent
import com.example.mvi_clean_demo.sections.blog.presentation.components.PostChatBubble
import com.example.mvi_clean_demo.sections.blog.presentation.components.PostChatBubblePlaceholder
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.PostSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposePosts(
    userId: Int,
    model: PostsViewModel.Model,
    sendEvent: (PostsViewModel.Event) -> Unit
) {
    val postsResponseStateUpdated by rememberUpdatedState(model.postEntriesModelsResponseState)

    LogComposeLifecycleEvent("ComposePosts")

    ComposeLifecycleEvent(
        onCreate = {
            Log.d("ComposeLifecycleEvent", "ComposePosts ComposeLifecycleEvent onResume() postsResponseStateUpdated $postsResponseStateUpdated")
            if (postsResponseStateUpdated is Idle) {
                sendEvent(PostsViewModel.Event.GetPostEntriesFromUser(userId))
                Log.d("ComposeLifecycleEvent", "ComposePosts ComposeLifecycleEvent onResume() sendEvent(GetPostEntriesFromUser(userId)) called")
            }
        }
    )

    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            if (model.postEntriesModelsResponseState is Success) {
                val items = model.postEntriesModelsResponseState.data
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(items) { entryModel ->
                        PostChatBubble(
                            postEntryModel = entryModel,
                            isUserMe = items.indexOf(entryModel) % 2 == 0
                        )
                    }
                }
            }
            if (model.isLoading) {
                PostChatBubbleCardsPlaceholder()
            }
        }
    }
}

@Composable
private fun PostChatBubbleCardsPlaceholder() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(5) { index ->
            PostChatBubblePlaceholder(
                isUserMe = index % 2 == 0
            )
        }
    }
}


@Preview(
    name = "Light Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = Devices.PIXEL_3A
)
@Preview(
    name = "Dark Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL_3A
)
@Composable
fun PostsPreview() {
    val model = PostsViewModel.Model(
        isLoading = false,
        postEntriesModelsResponseState = Success(data = PostSampleData.models)
    )
    ComposeUnitConverterTheme {
        ComposePosts(
            userId = 1,
            model = model,
            sendEvent = { }
        )
    }
}

