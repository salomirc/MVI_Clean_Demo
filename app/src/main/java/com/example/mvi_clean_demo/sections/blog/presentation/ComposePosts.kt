package com.example.mvi_clean_demo.sections.blog.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.common.ui_components.LoadingScreen
import com.example.mvi_clean_demo.sections.blog.domain.model.PostEntry
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposePosts(
    userId: Int,
    model: PostsViewModel.Model,
    sendEvent: (PostsViewModel.Event) -> Unit
) {
    val responseState = model.postEntries
    LaunchedEffect(responseState) {
        val shouldGetUsers = ((responseState is Idle) || (responseState is Failure))
        if (shouldGetUsers) {
            sendEvent(PostsViewModel.Event.GetPostEntriesFromUser(userId))
        }
    }
    if (model.isLoading) {
        LoadingScreen()
    } else {
        if (responseState is Success) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(responseState.data) { entry ->
                    PostCard(entry = entry)
                }
            }
        }
    }
}

@Composable
fun PostCard(entry: PostEntry) {
    Card(
        modifier = Modifier
            .padding(16.dp, 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "userId: ${entry.userId}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "id: ${entry.id}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.title,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = entry.body,
                style = MaterialTheme.typography.bodyMedium
            )
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
        postEntries = Idle
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

