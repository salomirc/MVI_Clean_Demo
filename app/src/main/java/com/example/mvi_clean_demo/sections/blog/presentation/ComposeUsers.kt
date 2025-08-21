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
import com.example.mvi_clean_demo.sections.blog.domain.model.User
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme

@Composable
fun ComposeUsers(
    model: UsersViewModel.Model,
    sendEvent: (UsersViewModel.Event) -> Unit,
    onNavigateToUserPosts: (Int) -> Unit
) {
    val responseState = model.users
    LaunchedEffect(responseState) {
        val shouldGetUsers = ((responseState is Idle) || (responseState is Failure))
        if (shouldGetUsers) {
            sendEvent(UsersViewModel.Event.GetUsers)
        }
    }
    if (model.isLoading) {
        LoadingScreen()
    } else {
        if (responseState is Success) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(responseState.data) { user ->
                    UserCard(
                        user = user,
                        onNavigateToUserPosts = onNavigateToUserPosts
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: User,
    onNavigateToUserPosts: (Int) -> Unit
) {
    Card(
        onClick = { onNavigateToUserPosts(user.id) },
        modifier = Modifier
            .padding(16.dp, 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Name: ${user.name}, id: ${user.id}",
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Username: ${user.username}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Email: ${user.email}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Address: \n" +
                        "\tStreet: ${user.address.street}\n" +
                        "\tSuite: ${user.address.suite}\n" +
                        "\tCity ${user.address.city}\n" +
                        "\tZipcode: ${user.address.zipcode}\n" +
                        "\tGeo: \n" +
                        "\t\tLat: ${user.address.geo.lat}\n" +
                        "\t\tLongitude: ${user.address.geo.lng}\n" +
                        "\tPhone: ${user.phone}\n" +
                        "\tWebsite: ${user.website}\n" +
                        "\tCompany: \n" +
                        "\t\tName: ${user.company.name}\n" +
                        "\t\tCatchPhrase: ${user.company.catchPhrase}\n" +
                        "\t\tBS: ${user.company.bs}",
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
fun UsersPreview() {
    val model = UsersViewModel.Model(
        isLoading = false,
        users = Idle
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

