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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
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
import com.example.mvi_clean_demo.screens.NavigationItemModel.Distance
import com.example.mvi_clean_demo.screens.NavigationItemModel.Temperature
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
        snackbarHost = { SnackbarHost(snackBarHostState) }
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
    appContainer: AppContainer,
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
            val viewModel = viewModel<TemperatureViewModel>(
                factory = appContainer.provideTemperatureViewModelFactory(initialTempValue)
            ).also { Log.d("VM", "NavBackStackEntry = $it") }
            val model by viewModel.modelStateFlow.collectAsStateWithLifecycle()
            TemperatureConverter(
                model = model,
                sendEvent = { event ->
                    viewModel.sendEvent(event)
                }
            )
        }
        composable<DistancesDestination> {
            val viewModel = viewModel<DistancesViewModel>(
                factory = appContainer.provideDistancesViewModelFactory()
            ).also { Log.d("VM", "NavBackStackEntry = $it") }
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
        }
    }
}

inline fun <reified T: NavigationItemModel.Destination> NavBackStackEntry.isSuccessfulToRoute(): Boolean {
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
    val appContainer = AppContainer(LocalContext.current.applicationContext)
    ComposeUnitConverterTheme {
        ComposeUnitConverter(
            appContainer = appContainer,
            onNavigateBack = { }
        )
    }
}