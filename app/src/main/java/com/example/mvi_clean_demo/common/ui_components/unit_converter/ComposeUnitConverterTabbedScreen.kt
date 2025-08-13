package com.example.mvi_clean_demo.common.ui_components.unit_converter

import android.content.res.Configuration
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mvi_clean_demo.R
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.DistancesNavTarget
import com.example.mvi_clean_demo.common.ui_components.unit_converter.UnitConverterNavTarget.TemperatureNavTarget
import com.example.mvi_clean_demo.screens.DistancesConverter
import com.example.mvi_clean_demo.screens.NavigationItemModel.Distance
import com.example.mvi_clean_demo.screens.NavigationItemModel.Temperature
import com.example.mvi_clean_demo.screens.TemperatureConverter
import com.example.mvi_clean_demo.theme.ComposeUnitConverterTheme
import com.example.mvi_clean_demo.viewmodels.DistancesViewModel
import com.example.mvi_clean_demo.viewmodels.MainViewModel
import com.example.mvi_clean_demo.viewmodels.TemperatureViewModel
import kotlinx.coroutines.launch

@Composable
fun UnitConverterTabbedScreen(
    model: MainViewModel.Model,
    sendEvent: (MainViewModel.Event) -> Unit,
    onNavigateBack: () -> Unit,
    onNextButton: () -> Unit
) {
    val navController = rememberNavController()
    ComposeUnitConverter(
        model = model,
        navController = navController,
        onNavigateBack = onNavigateBack,
        content = { innerPadding ->
            ComposeUnitConverterNavHost(
                sendEvent = sendEvent,
                navController = navController,
                modifier = Modifier.padding(innerPadding),
                onNextButton = onNextButton
            )
        }
    )
}

@Composable
fun ComposeUnitConverter(
    model: MainViewModel.Model,
    navController: NavHostController,
    onNavigateBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val textMenuItems = listOf("Item #1", "Item #2")
    val snackBarCoroutineScope = rememberCoroutineScope()
    val snackBarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ComposeUnitConverterTopBar(
                textMenuItems = textMenuItems,
                navTitle = model.navigationTitle,
                onBack = onNavigateBack,
                onClick = { s ->
                    snackBarCoroutineScope.launch {
                        snackBarHostState.showSnackbar(message = s)
                    }
                }
            )
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
                selected = currentDestinationRoute?.contains(model.navTarget.javaClass.simpleName) == true,
                onClick = {
                    navController.navigate(model.navTarget) {
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
                    startDestination = Temperature.navTarget,
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable<TemperatureNavTarget> { backStackEntry ->
                        val initialTempValue =
                            backStackEntry.toRoute<TemperatureNavTarget>().initialTempValue
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

                    composable<DistancesNavTarget> { backStackEntry ->
                        val model = DistancesViewModel.Model(
                            distance = "100",
                            unit = R.string.meter,
                            isButtonEnabled = false
                        )
                        DistancesConverter(
                            model = model,
                            sendEvent = {},
                            onNextButton = {
                                navController.navigate(TemperatureNavTarget("200"))
                            }
                        )
                    }
                }
            },
            model = MainViewModel.Model(
                messageResourceIdWrapper = null,
                navigationTitle = stringResource(Temperature.label)
            )
        )
    }
}