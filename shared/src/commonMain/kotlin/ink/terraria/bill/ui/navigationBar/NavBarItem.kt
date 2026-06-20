package ink.terraria.bill.ui.navigationBar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.ui.graphics.vector.ImageVector
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.bill
import bill.shared.generated.resources.ledger
import bill.shared.generated.resources.statistic
import ink.terraria.bill.ui.bill.BillDestination
import ink.terraria.bill.ui.ledger.LedgerDestination
import ink.terraria.bill.ui.navigation.NavigationDestination
import ink.terraria.bill.ui.statistic.StatisticDestination
import org.jetbrains.compose.resources.StringResource

data class NavBarItem(
    val destination: NavigationDestination,
    val labelRes: StringResource,
    val icon: ImageVector,
)

val navBarItems = listOf(
    NavBarItem(BillDestination, Res.string.bill, Icons.Default.AttachMoney),
    NavBarItem(StatisticDestination, Res.string.statistic, Icons.Default.BarChart),
    NavBarItem(LedgerDestination, Res.string.ledger, Icons.Default.AccountBalanceWallet)
)

val navBarItemsWithoutLedger = listOf(
    NavBarItem(LedgerDestination, Res.string.ledger, Icons.Default.AccountBalanceWallet)
)
