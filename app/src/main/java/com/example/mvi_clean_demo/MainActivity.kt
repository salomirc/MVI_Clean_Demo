package com.example.mvi_clean_demo

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mvi_clean_demo.di.AppContainer
import com.example.mvi_clean_demo.di.MyApplication
import com.example.mvi_clean_demo.screens.DistancesConverter
import com.example.mvi_clean_demo.screens.NavigationItemModel
import com.example.mvi_clean_demo.screens.NavigationItemModel.Destination.DistancesDestination
import com.example.mvi_clean_demo.screens.NavigationItemModel.Destination.TemperatureDestination
import com.example.mvi_clean_demo.screens.TemperatureConverter
import com.example.mvi_clean_demo.ui.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appContainer = (applicationContext as MyApplication).appContainer
        enableEdgeToEdge()
        setContent {
            ComposeUnitConverterTheme {
                ComposeUnitConverter(
                    appContainer = appContainer,
                    onNavigateBack = { onBackPressedDispatcher.onBackPressed() }
                )
            }
        }
    }
}

@Composable
fun ComposeUnitConverter(
    appContainer: AppContainer,
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()
    val menuItems = listOf("Item #1", "Item #2")
    val snackbarCoroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ComposeUnitConverterTopBar(menuItems, onNavigateBack) { s ->
                snackbarCoroutineScope.launch {
                    snackbarHostState.showSnackbar(s)
                }
            }
        },
        bottomBar = {
            ComposeUnitConverterBottomBar(navController)
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        ComposeUnitConverterNavHost(
            appContainer = appContainer,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeUnitConverterTopBar(
    menuItems: List<String>,
    onBack: () -> Unit,
    onClick: (String) -> Unit
) {
    var menuOpened by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "",
                    tint = MaterialTheme.colorScheme.tertiary
                )
            }
        },
        actions = {
            Box {
                IconButton(
                    onClick = { menuOpened = true }
                ) {
                    Icon(Icons.Default.MoreVert, "")
                }
                DropdownMenu(
                    expanded = menuOpened,
                    onDismissRequest = { menuOpened = false }
                ) {
                    menuItems.forEachIndexed { index, s ->
                        if (index > 0) HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text(s) },
                            onClick = {
                                menuOpened = false
                                onClick(s)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ComposeUnitConverterBottomBar(navController: NavHostController) {
    val barItemModels = listOf(
        NavigationItemModel.Temperature,
        NavigationItemModel.Distances
    )
    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestinationRoute = navBackStackEntry?.destination?.route
        barItemModels.forEach { model ->
            NavigationBarItem(
//                selected = currentDestinationRoute == model.destination.toString(),
                selected = currentDestinationRoute?.contains(model.destination.javaClass.simpleName) == true,
                onClick = {
                    navController.navigate(model.destination) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                label = {
                    Text(text = stringResource(id = model.label))
                },
                icon = {
                    Icon(
                        imageVector = model.icon,
                        contentDescription = stringResource(id = model.label)
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}

@Composable
fun ComposeUnitConverterNavHost(
    appContainer: AppContainer,
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavigationItemModel.Temperature.destination,
        modifier = modifier
    ) {
        composable<TemperatureDestination> { backStackEntry  ->
            val initialTempValue = backStackEntry.toRoute<TemperatureDestination>().value
            TemperatureConverter(
                viewModel = viewModel<TemperatureViewModel>(
                    factory = appContainer.provideTemperatureViewModelFactory(initialTempValue)
                ).also { Log.d("VM", "NavBackStackEntry = $it") },
                navController = navController
            )
        }
        composable<DistancesDestination> {
            DistancesConverter(
                viewModel = viewModel<DistancesViewModel>(
                    factory = appContainer.provideDistancesViewModelFactory()
                ).also { Log.d("VM", "DistancesViewModel = $it") },
                navController = navController
            )
        }
    }
}

@Preview(
    name = "Light Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL
)
@Preview(
    name = "Dark Mode",
    group = "FullScreen",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    showSystemUi = true,
    device = Devices.PIXEL
)
@Composable
fun ComposeUnitConverterPreview() {
    val appContainer = AppContainer(LocalContext.current.applicationContext)
    ComposeUnitConverterTheme {
        ComposeUnitConverter(
            appContainer = appContainer,
            onNavigateBack = { }
        )
    }
}