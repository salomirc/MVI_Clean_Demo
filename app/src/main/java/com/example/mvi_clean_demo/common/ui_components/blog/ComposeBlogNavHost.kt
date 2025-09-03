package com.example.mvi_clean_demo.common.ui_components.blog

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mvi_clean_demo.MainViewModel
import com.example.mvi_clean_demo.MainViewModel.Event.SetNavigationTitle
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.common.ui_components.blog.BlogNavTarget.PostsNavTarget
import com.example.mvi_clean_demo.common.ui_components.blog.BlogNavTarget.UsersNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.LogNavigation
import com.example.mvi_clean_demo.sections.blog.presentation.ComposePosts
import com.example.mvi_clean_demo.sections.blog.presentation.ComposeUsers
import com.example.mvi_clean_demo.sections.blog.presentation.PostsViewModel
import com.example.mvi_clean_demo.sections.blog.presentation.UsersViewModel
import kotlinx.serialization.Serializable

sealed interface BlogNavTarget {
    @Serializable
    data object UsersNavTarget: BlogNavTarget
    @Serializable
    data class PostsNavTarget(val userId: Int) : BlogNavTarget
}

@Composable
fun ComposeBlogNavHost(
    sendEvent: (MainViewModel.Event) -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val animationDuration = 500
    NavHost(
        navController = navController,
        startDestination = UsersNavTarget,
        modifier = modifier,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth }, // from right to left
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth/4 }, // from left to right
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = LinearOutSlowInEasing
                )
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth/4 }, // from right to left
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutLinearInEasing
                )
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth }, // from left to right
                animationSpec = tween(
                    durationMillis = animationDuration,
                    easing = FastOutLinearInEasing
                )
            )
        }
    ) {
        composable<UsersNavTarget> { backStackEntry ->
            val viewModel: UsersViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = R.string.users_screen_title)))
            ComposeUsers(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNavigateToUserPosts = { userId ->
                    navController.navigate(PostsNavTarget(userId))
                },
            )
            LogNavigation(backStackEntry, viewModel)
        }

        composable<PostsNavTarget> { backStackEntry ->
            val userId = backStackEntry.toRoute<PostsNavTarget>().userId
            val viewModel: PostsViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = R.string.posts_screen_title)))
            ComposePosts(
                userId = userId,
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                }
            )
            LogNavigation(backStackEntry, viewModel)
        }
    }
}