package ink.terraria.bill.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.app_name
import bill.shared.generated.resources.switch_ledger
import bill.shared.generated.resources.yuan
import ink.terraria.bill.model.Ledger
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun BillAppBar(
    ledgers: List<Ledger>,
    selectLedgerId: Int?,
    balance: BigDecimal,
    onSelectLedgerClick: (Int) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            AppBarTitle(
                ledgers = ledgers,
                selectLedgerId = selectLedgerId,
                onSelectLedgerClick = onSelectLedgerClick
            )
        },
        subtitle = {
            Text(
                text = balance.toString() + stringResource(Res.string.yuan),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
fun AppBarTitle(
    ledgers: List<Ledger>,
    selectLedgerId: Int?,
    onSelectLedgerClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLedger = ledgers.find { ledger -> ledger.id == selectLedgerId }

    if (currentLedger == null) {
        Text(stringResource(Res.string.app_name))
        return
    }

    var ledgerMenuExpanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (!ledgerMenuExpanded) 0f else 90f,
        label = "rotation"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) {
                ledgerMenuExpanded = !ledgerMenuExpanded
            }
            .padding(end = 8.dp)
    ) {
        Text(
            text = currentLedger.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = stringResource(Res.string.switch_ledger),
            modifier = Modifier.rotate(rotation)
        )
    }
    DropdownMenu(
        expanded = ledgerMenuExpanded,
        onDismissRequest = { ledgerMenuExpanded = false },
        modifier =
            Modifier.heightIn(max = 240.dp)

    ) {
        for (ledger in ledgers) {
            LedgerMenuItem(
                ledger = ledger,
                selected = ledger.id == selectLedgerId,
                onSelectClick = { id ->
                    ledgerMenuExpanded = false
                    onSelectLedgerClick(id)
                }
            )
        }
    }
}

@Composable
fun LedgerMenuItem(
    ledger: Ledger,
    selected: Boolean,
    onSelectClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    DropdownMenuItem(
        text = { Text(ledger.name) },
        onClick = {
            onSelectClick(ledger.id)
        },
        modifier = modifier.padding(horizontal = 8.dp)
            .clip(MaterialTheme.shapes.small),
        trailingIcon = {
            if (selected) {
                Icon(Icons.Outlined.Check, contentDescription = null)
            }
        },
    )
}
