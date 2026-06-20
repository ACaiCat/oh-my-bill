package ink.terraria.bill.ui.navigationBar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@Composable
fun BillNavigationBar(
    currentRoute: String?,
    onItemClick: (String) -> Unit,
    withoutLedger: Boolean,
    modifier: Modifier = Modifier
) {
    NavigationBar(modifier = modifier) {
        val items = if (withoutLedger) navBarItemsWithoutLedger else navBarItems
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                label = { Text(stringResource(item.labelRes)) },
                selected = currentRoute == item.destination.route,
                onClick = { onItemClick(item.destination.route) },
            )
        }
    }
}

@Composable
fun BillNavigationRail(
    currentRoute: String?,
    onNavigationClick: (String) -> Unit,
    withoutLedger: Boolean,
    modifier: Modifier = Modifier
) {
    NavigationRail(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight(),
        ) {
            val items = if (withoutLedger) navBarItemsWithoutLedger else navBarItems
            items.forEach { item ->
                NavigationRailItem(
                    icon = { Icon(item.icon, contentDescription = stringResource(item.labelRes)) },
                    label = { Text(stringResource(item.labelRes)) },
                    selected = currentRoute == item.destination.route,
                    onClick = { onNavigationClick(item.destination.route) },
                )
            }
        }
    }
}
