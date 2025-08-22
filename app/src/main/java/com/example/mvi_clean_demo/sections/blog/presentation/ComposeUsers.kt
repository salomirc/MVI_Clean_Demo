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
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposeUsers(
    model: UsersViewModel.Model,
    sendEvent: (UsersViewModel.Event) -> Unit,
    onNavigateToUserPosts: (Int) -> Unit
) {
    val usersResponseState = model.users
    LaunchedEffect(usersResponseState) {
        val shouldGetUsers = ((usersResponseState is Idle) || (usersResponseState is Failure))
        if (shouldGetUsers) {
            sendEvent(UsersViewModel.Event.GetUsers)
        }
    }
    if (model.isLoading) {
        LoadingScreen()
    } else {
        if (usersResponseState is Success) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(usersResponseState.data) { cardModel ->
                    UserTierCard(
                        cardModel = cardModel,
                        onInfoLinkAction = {},
                        onCallButtonAction = {},
                        onNavigateToUserPosts = onNavigateToUserPosts,
                    )
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
fun UsersPreview() {
    val model = UsersViewModel.Model(
        isLoading = false,
        users = Success(data = UsersSampleData.models)
    )
    ComposeUnitConverterTheme {
        Surface {
            ComposeUsers(
                model = model,
                sendEvent = { },
                onNavigateToUserPosts = { }
            )
        }
    }
}

