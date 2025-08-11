package com.example.mvi_clean_demo

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mvi_clean_demo.screens.Destination
import com.example.mvi_clean_demo.screens.Destination.DistancesDestination
import com.example.mvi_clean_demo.screens.Destination.TemperatureDestination
import com.example.mvi_clean_demo.screens.DistancesConverter
import com.example.mvi_clean_demo.screens.NavigationItemModel.Distance
import com.example.mvi_clean_demo.screens.NavigationItemModel.Temperature
import com.example.mvi_clean_demo.screens.TemperatureConverter
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeUnitConverterTheme {
                ComposeUnitConverterWrapper(
                    onNavigateBack = { onBackPressedDispatcher.onBackPressed() },
                )
            }
        }
    }
}

@Composable
fun ComposeUnitConverterWrapper(
    onNavigateBack: () -> Unit
) {
    val navController = rememberNavController()
    ComposeUnitConverter(
        navController = navController,
        onNavigateBack = onNavigateBack,
        content = { innerPadding ->
            ComposeUnitConverterNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    )
}

@Composable
fun ComposeUnitConverter(
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val textMenuItems = listOf("Item #1", "Item #2")
    val snackBarCoroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }
    var navTitle by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(navController) {
        navController.currentBackStackEntryFlow.collect { backStackEntry ->
            val titleResId = backStackEntry.run {
                when {
                    isSuccessfulToRoute<TemperatureDestination>() -> Temperature.label
                    isSuccessfulToRoute<DistancesDestination>() -> Distance.label
                    else -> null
                }
            }
            titleResId?.let { stringResId ->
                navTitle = context.getString(stringResId)
            }
            Log.d("NavChange", "Navigated to title = $navTitle")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ComposeUnitConverterTopBar(
                textMenuItems = textMenuItems,
                navTitle = navTitle,
                onBack = onNavigateBack
            ) { s ->
                snackBarCoroutineScope.launch {
                    snackBarHostState.showSnackbar(message = s)
                }
            }
        },
        bottomBar = {
            ComposeUnitConverterBottomBar(
                navController = navController
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeUnitConverterTopBar(
    textMenuItems: List<String>,
    navTitle: String,
    onBack: () -> Unit,
    onClick: (String) -> Unit
) {
    var menuOpened by remember { mutableStateOf(false) }
    TopAppBar(
        title = { Text(text = navTitle) },
        navigationIcon = {
            IconButton(
                onClick = onBack
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "",
                    tint = MaterialTheme.colorScheme.primary
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
                    textMenuItems.forEachIndexed { index, textItem ->
                        if (index > 0) HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text(textItem) },
                            onClick = {
                                menuOpened = false
                                onClick(textItem)
                            }
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ComposeUnitConverterBottomBar(
    navController: NavHostController
) {
    val barItemModels = remember { listOf(Temperature, Distance) }
    BottomAppBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestinationRoute = navBackStackEntry?.destination?.route
        barItemModels.forEach { model ->
            NavigationBarItem(
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
                        // re-selecting the same item
                        launchSingleTop = true
                        // Restore state when re-selecting a previously selected item
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
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Temperature.destination,
        modifier = modifier
    ) {
        composable<TemperatureDestination> { backStackEntry  ->
            val initialTempValue = backStackEntry.toRoute<TemperatureDestination>().initialTempValue
            val viewModel: TemperatureViewModel = hiltViewModel(
            creationCallback = { factory: TemperatureViewModel.Factory ->
                factory.create(initialTempValue) // Provide the initial temperature
            })
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            TemperatureConverter(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                }
            )
            LogNavigation(backStackEntry, viewModel)
        }

        composable<DistancesDestination> { backStackEntry ->
            val viewModel: DistancesViewModel = hiltViewModel()
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            DistancesConverter(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                },
                onNextButton = {
                    navController.navigate(TemperatureDestination("200"))
                }
            )
            LogNavigation(backStackEntry, viewModel)
        }
    }
}

@Composable
private fun <T: ViewModel> LogNavigation(backStackEntry: NavBackStackEntry, viewModel: T) {
    LaunchedEffect(backStackEntry) {
        Log.d("Navigation", "NavBackStackEntry = ${backStackEntry.destination.route}")
        Log.d("Navigation", "ViewModel = $viewModel")
    }
}

inline fun <reified T: Destination> NavBackStackEntry.isSuccessfulToRoute(): Boolean {
    return runCatching<T> {
        this.toRoute<T>()
    }.isSuccess
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
    ComposeUnitConverterTheme {
        val navController: NavHostController = rememberNavController()
        ComposeUnitConverter(
            navController = navController,
            onNavigateBack = {},
            content = { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = Temperature.destination,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable<TemperatureDestination> { backStackEntry  ->
                        val initialTempValue = backStackEntry.toRoute<TemperatureDestination>().initialTempValue
                        val model = TemperatureViewModel.Model(
                            temperature = initialTempValue,
                            scale = R.string.celsius,
                            isButtonEnabled = true
                        )
                        TemperatureConverter(
                            model = model,
                            sendEvent = {}
                        )
                    }

                    composable<DistancesDestination> { backStackEntry ->
                        val model = DistancesViewModel.Model(
                            distance = "100",
                            unit = R.string.meter,
                            isButtonEnabled = false
                        )
                        DistancesConverter(
                            model = model,
                            sendEvent = {},
                            onNextButton = {
                                navController.navigate(TemperatureDestination("200"))
                            }
                        )
                    }
                }
            }
        )
    }
}