package ink.terraria.bill.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ink.terraria.bill.ui.bill.BillDestination
import ink.terraria.bill.ui.bill.BillScreen
import ink.terraria.bill.ui.bill.BillScreenViewModel
import ink.terraria.bill.ui.ledger.LedgerDestination
import ink.terraria.bill.ui.ledger.LedgerScreen
import ink.terraria.bill.ui.ledger.LedgerScreenViewModel
import ink.terraria.bill.ui.statistic.StatisticDestination
import ink.terraria.bill.ui.statistic.StatisticScreen
import ink.terraria.bill.ui.statistic.StatisticScreenViewModel

@Composable
fun BillNavHost(
    navController: NavHostController,
    billScreenViewModel: BillScreenViewModel,
    statisticScreenViewModel: StatisticScreenViewModel,
    ledgerScreenViewModel: LedgerScreenViewModel,
    topAppBarScrollBehavior: TopAppBarScrollBehavior,
    direction: Int,
    isHorizontal: Boolean,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = BillDestination.route,
        enterTransition = {
            if (isHorizontal) {
                slideInHorizontally(
                    initialOffsetX = { it * direction },
                    animationSpec = tween(300)
                )
            } else {
                fadeIn(animationSpec = tween(0))
            }
        },
        exitTransition = {
            if (isHorizontal) {
                slideOutHorizontally(
                    targetOffsetX = { -it * direction },
                    animationSpec = tween(300)
                )
            } else {
                fadeOut(animationSpec = tween(0))
            }
        },
        modifier = modifier
    ) {
        composable(
            route = BillDestination.route,
        ) {
            val uiState by billScreenViewModel.uiState.collectAsState()
            BillScreen(
                uiState = uiState,
                onSelectLedgerClick = billScreenViewModel::selectLedger,
                onAddBill = billScreenViewModel::addBill,
                onDeleteBill = billScreenViewModel::deleteBill,
                onUpdateBill = billScreenViewModel::updateBill,
                scrollBehavior = topAppBarScrollBehavior,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = StatisticDestination.route,
        ) {
            val uiState by statisticScreenViewModel.uiState.collectAsState()
            StatisticScreen(
                uiState = uiState,
                onSelectLedgerClick = statisticScreenViewModel::selectLedger,
                onSelectMonthClick = statisticScreenViewModel::selectMonth,
                scrollBehavior = topAppBarScrollBehavior,
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(
            route = LedgerDestination.route
        ) {
            val uiState by ledgerScreenViewModel.uiState.collectAsState()
            LedgerScreen(
                uiState = uiState,
                onAddLedger = ledgerScreenViewModel::addLedger,
                onDeleteLedger = ledgerScreenViewModel::deleteLedger,
                onUpdateLedger = ledgerScreenViewModel::updateLedger,
                scrollBehavior = topAppBarScrollBehavior,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
