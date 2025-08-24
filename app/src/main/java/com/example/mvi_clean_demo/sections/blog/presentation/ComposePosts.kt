package com.example.mvi_clean_demo.sections.blog.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.common.ui_components.LoadingScreen
import com.example.mvi_clean_demo.sections.blog.presentation.components.ComposePostCard
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.PostSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposePosts(
    userId: Int,
    model: PostsViewModel.Model,
    sendEvent: (PostsViewModel.Event) -> Unit
) {
    val postResponseState = model.postEntries
    LaunchedEffect(postResponseState) {
        val shouldGetUsers = ((postResponseState is Idle) || (postResponseState is Failure))
        if (shouldGetUsers) {
            sendEvent(PostsViewModel.Event.GetPostEntriesFromUser(userId))
        }
    }
    if (model.isLoading) {
        LoadingScreen()
    } else {
        if (postResponseState is Success) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(postResponseState.data) { entryModel ->
                    ComposePostCard(postEntryModel = entryModel)
                }
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
        postEntries = Success(data = PostSampleData.models)
    )
    ComposeUnitConverterTheme {
        Surface {
            ComposePosts(
                userId = 1,
                model = model,
                sendEvent = { }
            )
        }
    }
}

