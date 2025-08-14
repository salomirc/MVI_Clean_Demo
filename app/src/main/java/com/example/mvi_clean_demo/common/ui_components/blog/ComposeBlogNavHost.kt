package com.example.mvi_clean_demo.common.ui_components.blog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.blog.presentation.UsersViewModel
import com.example.mvi_clean_demo.common.ui_components.unit_converter.LogNavigation
import com.example.mvi_clean_demo.screens.ComposeUsers
import com.example.mvi_clean_demo.viewmodels.MainViewModel
import com.example.mvi_clean_demo.viewmodels.MainViewModel.Event.SetNavigationTitle
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
    NavHost(
        navController = navController,
        startDestination = BlogNavTarget.UsersNavTarget,
        modifier = modifier
    ) {
        composable<BlogNavTarget.UsersNavTarget> { backStackEntry  ->
            val viewModel: UsersViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            sendEvent(SetNavigationTitle(title = stringResource(id = R.string.users_screen_title)))
            ComposeUsers(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNextButton = { },
            )
            LogNavigation(backStackEntry, viewModel)
        }
    }
}