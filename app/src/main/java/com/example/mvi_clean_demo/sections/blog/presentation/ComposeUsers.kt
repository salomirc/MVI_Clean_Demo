package com.example.mvi_clean_demo.sections.blog.presentation

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Failure
import com.example.mvi_clean_demo.common.repository.ResponseState.ActiveResponseState.Success
import com.example.mvi_clean_demo.common.repository.ResponseState.Idle
import com.example.mvi_clean_demo.common.ui_components.ComposeRepeatOnLifecycle
import com.example.mvi_clean_demo.common.ui_components.LoadingBox
import com.example.mvi_clean_demo.sections.blog.presentation.components.ComposeUserTierCardOptimized
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.UsersSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposeUsers(
    model: UsersViewModel.Model,
    sendEvent: (UsersViewModel.Event) -> Unit,
    onNavigateToUserPosts: (Int) -> Unit
) {
    val usersResponseState = model.userCardModelsResponseState

    ComposeRepeatOnLifecycle(Lifecycle.State.RESUMED) {
        // This block runs only when lifecycle is at least RESUMED
        val shouldGetUsers = ((usersResponseState is Idle) || (usersResponseState is Failure))
        if (shouldGetUsers) {
            sendEvent(UsersViewModel.Event.GetUsers)
        }
    }

    Surface {
        Box(modifier = Modifier.fillMaxSize()) {
            if (usersResponseState is Success) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = usersResponseState.data,
                        key = { it.userModel.id },          // add stability to recycler
                        contentType = { "UserTierCard" }    // helps the internal recycling
                    ) { cardModel ->
                        ComposeUserTierCardOptimized(
                            model = cardModel,
                            sendEvent = sendEvent,
                            onInfoLinkAction = {},
                            onCallButtonAction = {},
                            onNavigateToUserPosts = onNavigateToUserPosts,
                        )
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
fun UsersPreview() {
    val model = UsersViewModel.Model(
        isLoading = false,
        userCardModelsResponseState = Success(data = UsersSampleData.models)
    )
    ComposeUnitConverterTheme {
        ComposeUsers(
            model = model,
            sendEvent = { },
            onNavigateToUserPosts = { }
        )
    }
}

