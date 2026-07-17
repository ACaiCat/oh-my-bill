package ink.terraria.bill.ui.ledger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.add_ledger
import bill.shared.generated.resources.delete_ledger
import bill.shared.generated.resources.delete_ledger_message
import bill.shared.generated.resources.ledger
import ink.terraria.bill.model.Ledger
import ink.terraria.bill.model.NewLedgerInput
import ink.terraria.bill.ui.AmountText
import ink.terraria.bill.ui.DeleteConfirmationDialog
import ink.terraria.bill.ui.EmptyList
import ink.terraria.bill.ui.SwipeToDeleteContainer
import ink.terraria.bill.ui.Title
import ink.terraria.bill.ui.navigation.NavigationDestination
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

object LedgerDestination : NavigationDestination {
    override val route: String = "ledger"
    override val titleRes: StringResource = Res.string.ledger
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedgerScreen(
    uiState: LedgerScreenUiState,
    onAddLedger: (NewLedgerInput) -> Unit,
    onDeleteLedger: (Int) -> Unit,
    onUpdateLedger: (Ledger) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    var showAddLedgerSheet by remember { mutableStateOf(false) }
    var editingLedger by remember { mutableStateOf<Ledger?>(null) }
    var ledgerToDelete by remember { mutableStateOf<Ledger?>(null) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = modifier,
        topBar = {
            LedgerAppBar(
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddLedgerSheet = true },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_ledger))
                Text(
                    text = stringResource(Res.string.add_ledger),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
    ) { paddingValues ->
        LedgerList(
            ledgers = uiState.ledgers,
            onLedgerClick = { ledger -> editingLedger = ledger },
            onLedgerDelete = { ledger -> ledgerToDelete = ledger },
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        )
    }

    if (showAddLedgerSheet || editingLedger != null) {
        AddLedgerSheet(
            ledger = editingLedger,
            onSave = { input ->
                val currentEditingLedger = editingLedger
                if (currentEditingLedger != null) {
                    onUpdateLedger(
                        currentEditingLedger.copy(
                            name = input.name,
                            balance = input.balance
                        )
                    )
                } else {
                    onAddLedger(input)
                }
                showAddLedgerSheet = false
                editingLedger = null
            },
            onCancel = {
                showAddLedgerSheet = false
                editingLedger = null
            },
            sheetState = sheetState
        )
    }

    ledgerToDelete?.let { ledger ->
        DeleteConfirmationDialog(
            onConfirm = {
                onDeleteLedger(ledger.id)
                ledgerToDelete = null
            },
            onDismiss = { ledgerToDelete = null },
            title = stringResource(Res.string.delete_ledger),
            message = stringResource(Res.string.delete_ledger_message, ledger.name)
        )
    }
}

@Composable
fun LedgerList(
    ledgers: List<Ledger>,
    onLedgerClick: (Ledger) -> Unit,
    onLedgerDelete: (Ledger) -> Unit,
    modifier: Modifier = Modifier
) {
    if (ledgers.isEmpty()) {
        EmptyList()
    }
    val listState = rememberLazyListState()
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 100.dp),
        state = listState,
        modifier = modifier
    ) {
        items(ledgers, key = { it.id }) { ledger ->
            SwipeToDeleteContainer(onDelete = { onLedgerDelete(ledger) }, modifier = Modifier.animateItem()) {
                LedgerItem(ledger, onClick = { onLedgerClick(ledger) })
            }
        }
    }
}

@Composable
fun LedgerItem(
    ledger: Ledger,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
                .padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Title(ledger.name)
            }
            AmountText(amount = ledger.balance, showSignal = false)
        }
    }
}
