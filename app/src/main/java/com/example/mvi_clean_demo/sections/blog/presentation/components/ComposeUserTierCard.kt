package com.example.mvi_clean_demo.sections.blog.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.sections.blog.presentation.UsersViewModel.Event
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.UsersSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.theme.clientTierCardSurface
import com.example.mvi_clean_demo.theme.clientTierInitialsSurface

@Composable
fun UserTierCard(
    model: UserCardModel,
    sendEvent: (Event) -> Unit,
    onInfoLinkAction: (url: String) -> Unit,
    onCallButtonAction: (phoneNumber: String) -> Unit,
    onNavigateToUserPosts: (Int) -> Unit,
) {
    val rotation by animateFloatAsState(
        targetValue = if (model.isExpanded) 0f else 180f,
        animationSpec = tween(1000),
        label = "UpArrowRotation"
    )
    val tierTextIconModel = model.tierModel.tierTextIconColor

    Card(
        onClick = {
            if (model.isExpanded) {
                onNavigateToUserPosts(model.userModel.id)
            } else {
                sendEvent(Event.UpdateUserCardModel(isExpanded = true, id = model.userModel.id))
            }
        },
        modifier = Modifier.padding(bottom = 16.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.clientTierCardSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = model.userModel.name,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleMedium
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Up Arrow",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable {
                            sendEvent(
                                Event.UpdateUserCardModel(
                                    isExpanded = !model.isExpanded,
                                    id = model.userModel.id
                                )
                            )
                        }
                        .rotate(rotation)
                )
            }
            Text(
                text = "Username: ${model.userModel.username}, id: ${model.userModel.id}",
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = "Email: ${model.userModel.email}",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (model.isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = model.tierModel.tierSurfaceColor
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(model.tierModel.tierIconRes),
                                    contentDescription = "Tier Icon",
                                    modifier = Modifier
                                        .size(48.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = model.tierModel.tierTitle,
                                    color = tierTextIconModel,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Image(
                                painter = painterResource(model.tierModel.tierLineRes),
                                contentDescription = "Separation line",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(
                                        horizontal = 12.dp,
                                        vertical = 6.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Find out more about your tier.",
                                    color = tierTextIconModel,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowUp,
                                    contentDescription = "Up Arrow",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .rotate(90f)
                                        .clickable {
                                            onInfoLinkAction(model.userModel.website)
                                        },
                                    tint = tierTextIconModel
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Premium manager contact",
                        maxLines = 1,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.clientTierInitialsSurface
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = model.userInitials,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = model.userModel.name,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = model.userModel.phone,
                                maxLines = 1,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Normal
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .weight(1f),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            IconButton(
                                onClick = {
                                    onCallButtonAction(model.userModel.phone)
                                },
                                modifier = Modifier
                                    .size(48.dp)
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.call_button),
                                    contentDescription = "Call Button",
                                    tint = Color.Unspecified
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    group = "List",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = "spec:width=420dp,height=1650dp,dpi=240"
)
@Preview(
    name = "Dark Mode",
    group = "List",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = "spec:width=420dp,height=1650dp,dpi=240"
)
@Composable
fun UserCardsPreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                for (model in UsersSampleData.models) {
                    UserTierCard(
                        model = model,
                        onInfoLinkAction = {},
                        onCallButtonAction = {},
                        onNavigateToUserPosts = {},
                        sendEvent = {}
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Light Mode",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    device = "spec:width=420dp,height=320dp,dpi=240"
)
@Preview(
    name = "Dark Mode",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = "spec:width=420dp,height=320dp,dpi=240"
)
@Composable
fun UserCardPreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                UserTierCard(
                    model = UsersSampleData.models.first(),
                    onInfoLinkAction = {},
                    onCallButtonAction = {},
                    onNavigateToUserPosts = {},
                    sendEvent = {}
                )
            }
        }
    }
}