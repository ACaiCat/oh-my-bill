package ink.terraria.bill

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.remember
import ink.terraria.bill.data.buildBillDatabase
import ink.terraria.bill.data.init
import ink.terraria.bill.ui.bill.BillScreenViewModel
import ink.terraria.bill.ui.ledger.LedgerScreenViewModel
import ink.terraria.bill.ui.statistic.StatisticScreenViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        init(applicationContext)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val database = remember { buildBillDatabase() }
            val selectedLedgerId = remember { MutableStateFlow<Int?>(null) }
            val billScreenViewModel = remember { BillScreenViewModel(database, selectedLedgerId) }
            val statisticScreenViewModel = remember {
                StatisticScreenViewModel(database, selectedLedgerId)
            }
            val ledgerScreenViewModel = remember { LedgerScreenViewModel(database) }

            App(
                windowSizeClass = windowSizeClass,
                billScreenViewModel = billScreenViewModel,
                statisticScreenViewModel = statisticScreenViewModel,
                ledgerScreenViewModel = ledgerScreenViewModel,
            )
        }
    }
}
