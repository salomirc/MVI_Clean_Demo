package com.example.mvi_clean_demo.sections.blog.presentation

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.common.ui_components.ComposeRepeatOnLifecycle
import com.example.mvi_clean_demo.common.ui_components.LoadingBox
import com.example.mvi_clean_demo.common.ui_components.LogComposeLifecycleEvent
import com.example.mvi_clean_demo.sections.blog.presentation.components.ComposePostCard
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.PostSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposePosts(
    userId: Int,
    model: PostsViewModel.Model,
    sendEvent: (PostsViewModel.Event) -> Unit
) {
    val postsResponseState = model.postEntriesModelsResponseState

    LogComposeLifecycleEvent("ComposePosts")

    ComposeRepeatOnLifecycle(Lifecycle.State.RESUMED) { state ->
        // This block runs only when lifecycle is at least RESUMED
        Log.d("RepeatOnLifecycle", "ComposePosts RepeatOnLifecycle $state")
        Log.d("RepeatOnLifecycle", "ComposePosts postResponseState $postsResponseState")
        val shouldGetUsers = ((postsResponseState is Idle) || (postsResponseState is Failure))
        if (shouldGetUsers) {
            sendEvent(PostsViewModel.Event.GetPostEntriesFromUser(userId))
            Log.d("RepeatOnLifecycle", "ComposePosts RepeatOnLifecycle sendEvent(GetPostEntriesFromUser(userId)) called $state")
        }
    }

    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            if (postsResponseState is Success) {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(postsResponseState.data) { entryModel ->
                        ComposePostCard(postEntryModel = entryModel)
                    }
                }
            }
            if (model.isLoading) {
                LoadingBox()
            }
        }
    }
}



@Preview(
    name = "Light Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = Devices.PIXEL
)
@Preview(
    name = "Dark Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL
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

