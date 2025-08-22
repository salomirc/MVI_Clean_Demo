@file:JvmName("ComposeUserCardKt")

package com.example.mvi_clean_demo.sections.blog.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel

@Composable
fun UserCard(
    cardModel: UserCardModel,
    onNavigateToUserPosts: (Int) -> Unit
) {
    val user = cardModel.user
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