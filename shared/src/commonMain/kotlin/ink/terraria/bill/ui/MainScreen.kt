package ink.terraria.bill.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ink.terraria.bill.ui.bill.BillDestination
import ink.terraria.bill.ui.bill.BillScreenViewModel
import ink.terraria.bill.ui.ledger.LedgerDestination
import ink.terraria.bill.ui.ledger.LedgerScreenViewModel
import ink.terraria.bill.ui.navigation.BillNavHost
import ink.terraria.bill.ui.navigationBar.BillNavigationBar
import ink.terraria.bill.ui.navigationBar.BillNavigationRail
import ink.terraria.bill.ui.navigationBar.navBarItems
import ink.terraria.bill.ui.statistic.StatisticDestination
import ink.terraria.bill.ui.statistic.StatisticScreenViewModel

@Composable
fun MainScreen(
    windowSizeClass: WindowSizeClass,
    billScreenViewModel: BillScreenViewModel,
    statisticScreenViewModel: StatisticScreenViewModel,
    ledgerScreenViewModel: LedgerScreenViewModel,
) {
    val navController = rememberNavController()
    val topAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val isCompact = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact

    val ledgerUiState by ledgerScreenViewModel.uiState.collectAsState()
    val withoutLedger = !ledgerUiState.isLoading && ledgerUiState.ledgers.isEmpty()

    val routes = navBarItems.map { it.destination.route }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        ?: navBarItems.first().destination.route

    var previousRoute by remember { mutableStateOf(currentRoute) }

    val direction = remember(currentRoute) {
        val previousIndex = routes.indexOf(previousRoute)
        val currentIndex = routes.indexOf(currentRoute)
        if (currentIndex > previousIndex) 1 else -1
    }

    LaunchedEffect(currentRoute) {
        previousRoute = currentRoute
    }

    LaunchedEffect(withoutLedger, navBackStackEntry?.destination?.route) {
        if (withoutLedger) {
            val route = navBackStackEntry?.destination?.route
            if (route == BillDestination.route || route == StatisticDestination.route) {
                navController.navigate(LedgerDestination.route) {
                    popUpTo(navController.graph.startDestinationId) {
                        inclusive = true
                    }
                }
            }
        }
    }

    if (isCompact) {
        Scaffold(
            bottomBar = {
                BillNavigationBar(
                    currentRoute = navBackStackEntry?.destination?.route,
                    onItemClick = { route ->
                        if (route == currentRoute) {
                            return@BillNavigationBar
                        }
                        navController.navigate(route) {
                            popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                                inclusive = true
                            }
                        }
                    },
                    withoutLedger = withoutLedger
                )
            },
            modifier = Modifier.fillMaxSize()
        ) { paddingValues ->
            BillNavHost(
                navController = navController,
                billScreenViewModel = billScreenViewModel,
                statisticScreenViewModel = statisticScreenViewModel,
                ledgerScreenViewModel = ledgerScreenViewModel,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                direction = direction,
                isHorizontal = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding() - 16.dp)
            )
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            BillNavigationRail(
                currentRoute = navBackStackEntry?.destination?.route,
                onNavigationClick = { route ->
                    if (route == currentRoute) {
                        return@BillNavigationRail
                    }
                    navController.navigate(route) {
                        popUpTo(navController.currentBackStackEntry?.destination?.route ?: "") {
                            inclusive = true
                        }
                    }
                },
                withoutLedger = withoutLedger
            )
            BillNavHost(
                navController = navController,
                billScreenViewModel = billScreenViewModel,
                statisticScreenViewModel = statisticScreenViewModel,
                ledgerScreenViewModel = ledgerScreenViewModel,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                direction = direction,
                isHorizontal = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
