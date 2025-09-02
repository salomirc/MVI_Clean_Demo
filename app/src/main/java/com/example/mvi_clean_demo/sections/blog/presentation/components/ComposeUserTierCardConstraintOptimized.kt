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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.example.mvi_clean_demo.sections.blog.domain.model.UserModel
import com.example.mvi_clean_demo.sections.blog.presentation.UsersViewModel.Event
import com.example.mvi_clean_demo.sections.blog.presentation.model.TierModel
import com.example.mvi_clean_demo.sections.blog.presentation.model.UserCardModel
import com.example.mvi_clean_demo.sections.blog.presentation.preview_sample_data.UsersSampleData
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.theme.clientTierCardSurface
import com.example.mvi_clean_demo.theme.clientTierInitialsSurface

@Composable
fun ComposeUserTierCardConstraintOptimized(
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
            // Header (always visible)
            UserCardHeader(
                model = model,
                sendEvent = sendEvent
            )

            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(visible = model.isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Tier info (only expanded)
                    TierSection(
                        tierModel = model.tierModel,
                        onInfoLinkAction = onInfoLinkAction,
                        website = model.userModel.website
                    )
                    // Manager contact (only expanded)
                    ManagerContact(
                        userModel = model.userModel,
                        userInitials = model.userInitials,
                        onCallButtonAction = onCallButtonAction
                    )
                }
            }
        }
    }
}

@Composable
private fun UserCardHeader(
    model: UserCardModel,
    sendEvent: (Event) -> Unit
) {
    val rotation by animateFloatAsState(
        targetValue = if (model.isExpanded) 0f else 180f,
        animationSpec = tween(1000),
        label = "ArrowRotation"
    )

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (name, arrow, username, email) = createRefs()

        Text(
            text = model.userModel.name,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(name) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(arrow.start)
                width = Dimension.fillToConstraints
            }
        )

        Icon(
            imageVector = Icons.Default.KeyboardArrowUp,
            contentDescription = "Expand",
            modifier = Modifier
                .size(32.dp)
                .rotate(rotation)
                .clickable {
                    sendEvent(
                        Event.UpdateUserCardModel(
                            isExpanded = !model.isExpanded,
                            id = model.userModel.id
                        )
                    )
                }
                .constrainAs(arrow) {
                    top.linkTo(name.top)
                    bottom.linkTo(name.bottom)
                    end.linkTo(parent.end)
                }
        )

        Text(
            text = "Username: ${model.userModel.username}, id: ${model.userModel.id}",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(username) {
                top.linkTo(name.bottom, margin = 4.dp)
                start.linkTo(parent.start)
            }
        )

        Text(
            text = "Email: ${model.userModel.email}",
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.constrainAs(email) {
                top.linkTo(username.bottom, margin = 4.dp)
                start.linkTo(parent.start)
            }
        )
    }
}

@Composable
private fun TierSection(
    tierModel: TierModel,
    onInfoLinkAction: (url: String) -> Unit,
    website: String
) {
    val tierIcon = painterResource(tierModel.tierIconRes)
    val tierLine = painterResource(tierModel.tierLineRes)

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = tierModel.tierSurfaceColor,
        modifier = Modifier.fillMaxWidth()
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (icon, title, line, moreText, moreIcon) = createRefs()

            Icon(
                painter = tierIcon,
                contentDescription = "Tier Icon",
                tint = Color.Unspecified,
                modifier = Modifier.constrainAs(icon) {
                    start.linkTo(parent.start, margin = 12.dp)
                    top.linkTo(parent.top, margin = 12.dp)
                }
            )

            Text(
                text = tierModel.tierTitle,
                color = tierModel.tierTextIconColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.constrainAs(title) {
                    start.linkTo(icon.end, margin = 12.dp)
                    top.linkTo(icon.top)
                    bottom.linkTo(icon.bottom)
                }
            )

            Image(
                painter = tierLine,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .height(1.dp)
                    .fillMaxWidth()
                    .constrainAs(line) {
                        top.linkTo(icon.bottom, margin = 12.dp)
                    }
            )

            Text(
                text = "Find out more about your tier.",
                color = tierModel.tierTextIconColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.constrainAs(moreText) {
                    top.linkTo(line.bottom, margin = 6.dp)
                    start.linkTo(parent.start, margin = 12.dp)
                }
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Info Link",
                tint = tierModel.tierTextIconColor,
                modifier = Modifier
                    .size(32.dp)
                    .rotate(90f)
                    .clickable { onInfoLinkAction(website) }
                    .constrainAs(moreIcon) {
                        top.linkTo(moreText.top)
                        bottom.linkTo(moreText.bottom)
                        end.linkTo(parent.end, margin = 12.dp)
                    }
            )
        }
    }
}

@Composable
private fun ManagerContact(
    userModel: UserModel,
    userInitials: String,
    onCallButtonAction: (phoneNumber: String) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val (title, initials, info, call) = createRefs()

        Text(
            text = "Premium manager contact",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            }
        )

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.clientTierInitialsSurface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier
                .size(48.dp)
                .constrainAs(initials) {
                    top.linkTo(title.bottom, margin = 8.dp)
                    start.linkTo(parent.start)
                }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = userInitials,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier.constrainAs(info) {
                start.linkTo(initials.end, margin = 12.dp)
                top.linkTo(initials.top)
                bottom.linkTo(initials.bottom)
                end.linkTo(call.start, margin = 8.dp)
                width = Dimension.fillToConstraints
            }
        ) {
            Text(
                text = userModel.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = userModel.phone,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        FilledIconButton(
            onClick = { onCallButtonAction(userModel.phone) },
            modifier = Modifier
                .size(48.dp)
                .constrainAs(call) {
                    end.linkTo(parent.end)
                    top.linkTo(initials.top)
                    bottom.linkTo(initials.bottom)
                }
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Call Button",
                tint = MaterialTheme.colorScheme.onPrimary
            )
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
fun ComposeUserTierCardsConstraintOptimizedPreview() {
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
                    ComposeUserTierCardConstraintOptimized(
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
fun ComposeUserTierCardConstraintOptimizedPreview() {
    ComposeUnitConverterTheme {
        var model by remember { mutableStateOf(UsersSampleData.models.first()) }
        Surface(color = MaterialTheme.colorScheme.background) {
            Box(modifier = Modifier.fillMaxSize()) {
                ComposeUserTierCardConstraintOptimized(
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