package ink.terraria.bill

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import ink.terraria.bill.ui.MainScreen
import ink.terraria.bill.ui.bill.BillScreenViewModel
import ink.terraria.bill.ui.ledger.LedgerScreenViewModel
import ink.terraria.bill.ui.statistic.StatisticScreenViewModel
import ink.terraria.bill.ui.theme.AppTheme

@Composable
fun App(
    windowSizeClass: WindowSizeClass,
    billScreenViewModel: BillScreenViewModel,
    statisticScreenViewModel: StatisticScreenViewModel,
    ledgerScreenViewModel: LedgerScreenViewModel,
) {
    AppTheme {
        MainScreen(
            windowSizeClass = windowSizeClass,
            billScreenViewModel = billScreenViewModel,
            statisticScreenViewModel = statisticScreenViewModel,
            ledgerScreenViewModel = ledgerScreenViewModel,
        )
    }
}
