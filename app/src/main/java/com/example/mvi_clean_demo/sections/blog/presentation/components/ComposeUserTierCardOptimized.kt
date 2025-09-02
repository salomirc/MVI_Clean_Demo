package com.example.mvi_clean_demo.sections.blog.presentation.components

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mvi_clean_demo.sections.blog.presentation.UsersViewModel.Event
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.UsersSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.theme.clientTierCardSurface
import com.example.mvi_clean_demo.theme.clientTierInitialsSurface

@Composable
fun ComposeUserTierCardOptimized(
    model: UserCardModel,
    sendEvent: (Event) -> Unit,
    onInfoLinkAction: (url: String) -> Unit,
    onCallButtonAction: (phoneNumber: String) -> Unit,
    onNavigateToUserPosts: (Int) -> Unit,
) {
    Card(
        onClick = {
            if (model.isExpanded) {
                onNavigateToUserPosts(model.userModel.id)
            } else {
                sendEvent(Event.UpdateUserCardModel(isExpanded = true, id = model.userModel.id))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.clientTierCardSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            UserHeader(model, sendEvent)
            UserDetails(model)
            AnimatedVisibility(visible = model.isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TierInfoSection(model, onInfoLinkAction)
                    ManagerContactSection(model, onCallButtonAction)
                }
            }
        }
    }
}

@Composable
private fun UserHeader(
    model: UserCardModel,
    sendEvent: (Event) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (model.isExpanded) 0f else 180f,
        animationSpec = tween(500),
        label = "UpArrowRotation"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = model.userModel.name,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = if (model.isExpanded) "Collapse" else "Expand",
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
}

@Composable
private fun UserDetails(model: UserCardModel) {
    Column(
        modifier = Modifier.padding(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Username: ${model.userModel.username}, id: ${model.userModel.id}",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "Email: ${model.userModel.email}",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TierInfoSection(
    model: UserCardModel,
    onInfoLinkAction: (String) -> Unit
) {
    val tierTextColor = model.tierModel.tierTextIconColor

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = model.tierModel.tierSurfaceColor
    ) {
        Column {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(model.tierModel.tierIconRes),
                    contentDescription = null, // decorativ
                    modifier = Modifier.size(48.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = model.tierModel.tierTitle,
                    color = tierTextColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
            }
            Image(
                painter = painterResource(model.tierModel.tierLineRes),
                contentDescription = null, // decorativ
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp),
                contentScale = ContentScale.FillWidth
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Find out more about your tier.",
                    color = tierTextColor,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Info Link",
                    modifier = Modifier
                        .size(32.dp)
                        .rotate(90f)
                        .clickable {
                            onInfoLinkAction(model.userModel.website)
                        },
                    tint = tierTextColor
                )
            }
        }
    }
}

@Composable
private fun ManagerContactSection(
    model: UserCardModel,
    onCallButtonAction: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "Premium manager contact",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.padding(4.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.clientTierInitialsSurface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(
                    modifier = Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = model.userInitials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = model.userModel.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = model.userModel.phone,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            FilledIconButton(
                onClick = { onCallButtonAction(model.userModel.phone) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call Button",
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
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
fun ComposeUserCardsPreview() {
    ComposeUnitConverterTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                for (model in UsersSampleData.models) {
                    ComposeUserTierCardOptimized(
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
    device = Devices.PIXEL
)
@Preview(
    name = "Dark Mode",
    group = "Single",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    device = Devices.PIXEL
)
@Composable
fun ComposeUserCardPreview() {
    ComposeUnitConverterTheme {
        var model by remember { mutableStateOf(UsersSampleData.models.first()) }
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                ComposeUserTierCardOptimized(
                    model = model,
                    onInfoLinkAction = {},
                    onCallButtonAction = {},
                    onNavigateToUserPosts = {},
                    sendEvent = {
                        when (it) {
                            is Event.UpdateUserCardModel -> {
                                model = model.copy(isExpanded = it.isExpanded)
                            }
                            else -> {}
                        }
                    }
                )
            }
        }
    }
}