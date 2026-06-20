package ink.terraria.bill

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import bill.desktopapp.generated.resources.Res
import bill.desktopapp.generated.resources.app_name
import bill.desktopapp.generated.resources.icon
import ink.terraria.bill.data.buildBillDatabase
import ink.terraria.bill.ui.bill.BillScreenViewModel
import ink.terraria.bill.ui.ledger.LedgerScreenViewModel
import ink.terraria.bill.ui.statistic.StatisticScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        icon = painterResource(Res.drawable.icon),
        title = stringResource(Res.string.app_name),
    ) {
        val windowSizeClass = calculateWindowSizeClass()
        val database = remember { buildBillDatabase() }
        val selectedLedgerId = remember { MutableStateFlow<Int?>(null) }
        val billScreenViewModel = remember { BillScreenViewModel(database, selectedLedgerId) }
        val statisticScreenViewModel =
            remember { StatisticScreenViewModel(database, selectedLedgerId) }
        val ledgerScreenViewModel = remember { LedgerScreenViewModel(database) }
        App(
            windowSizeClass = windowSizeClass,
            billScreenViewModel = billScreenViewModel,
            statisticScreenViewModel = statisticScreenViewModel,
            ledgerScreenViewModel = ledgerScreenViewModel,
        )
    }
}
