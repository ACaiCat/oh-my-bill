package ink.terraria.bill.ui.bill

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Badge
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
import bill.shared.generated.resources.add_bill
import bill.shared.generated.resources.app_name
import bill.shared.generated.resources.date_M
import bill.shared.generated.resources.date_MM_dd
import bill.shared.generated.resources.date_yyyy_M
import bill.shared.generated.resources.delete
import bill.shared.generated.resources.delete_bill_message
import ink.terraria.bill.isSameDay
import ink.terraria.bill.isSameMonth
import ink.terraria.bill.isSameYear
import ink.terraria.bill.model.Bill
import ink.terraria.bill.model.BillTag
import ink.terraria.bill.model.NewBillInput
import ink.terraria.bill.ui.AmountSection
import ink.terraria.bill.ui.BillAppBar
import ink.terraria.bill.ui.BillNote
import ink.terraria.bill.ui.DeleteConfirmationDialog
import ink.terraria.bill.ui.EmptyList
import ink.terraria.bill.ui.SwipeToDeleteContainer
import ink.terraria.bill.ui.TagIcon
import ink.terraria.bill.ui.TimeText
import ink.terraria.bill.ui.Title
import ink.terraria.bill.ui.navigation.NavigationDestination
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

object BillDestination : NavigationDestination {
    override val route: String = "bill"
    override val titleRes: StringResource = Res.string.app_name
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillScreen(
    uiState: BillScreenUiState,
    onSelectLedgerClick: (Int) -> Unit,
    onAddBill: (NewBillInput) -> Unit,
    onDeleteBill: (Int) -> Unit,
    onUpdateBill: (Bill) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    var showAddBillSheet by remember { mutableStateOf(false) }
    var editingBill by remember { mutableStateOf<Bill?>(null) }
    var billToDelete by remember { mutableStateOf<Bill?>(null) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    Scaffold(
        modifier = modifier,
        topBar = {
            BillAppBar(
                ledgers = uiState.ledgers,
                selectLedgerId = uiState.selectedLedgerId,
                onSelectLedgerClick = onSelectLedgerClick,
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    showAddBillSheet = true
                },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_bill))
                Text(
                    text = stringResource(Res.string.add_bill),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    ) { paddingValues ->
        BillList(
            uiState = uiState,
            onBillClick = { bill -> editingBill = bill },
            onBillDelete = { bill -> billToDelete = bill },
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        )
    }

    if (showAddBillSheet || editingBill != null) {
        AddBillSheet(
            bill = editingBill,
            onSave = { input ->
                val currentEditingBill = editingBill
                if (currentEditingBill != null) {
                    onUpdateBill(
                        currentEditingBill.copy(
                            title = input.title,
                            amount = input.amount,
                            tagId = input.tagId,
                            note = input.note,
                            time = input.time
                        )
                    )
                } else {
                    onAddBill(input)
                }
                showAddBillSheet = false
                editingBill = null
            },
            onCancel = {
                showAddBillSheet = false
                editingBill = null
            },
            sheetState = sheetState
        )
    }

    billToDelete?.let { bill ->
        DeleteConfirmationDialog(
            onConfirm = {
                onDeleteBill(bill.id)
                billToDelete = null
            },
            onDismiss = { billToDelete = null },
            title = stringResource(Res.string.delete),
            message = stringResource(Res.string.delete_bill_message, bill.title)
        )
    }
}

@Composable
fun BillList(
    uiState: BillScreenUiState,
    onBillClick: (Bill) -> Unit,
    onBillDelete: (Bill) -> Unit,
    modifier: Modifier = Modifier
) {
    if (uiState.bills.isEmpty()) {
        EmptyList()
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        itemsIndexed(uiState.bills, key = { _, bill -> bill.id }) { index, bill ->
            if (index == 0 || !isSameMonth(bill, uiState.bills[index - 1])) {
                MonthHeader(
                    time = bill.time,
                    showYear = index != 0 && !isSameYear(bill, uiState.bills[index - 1]),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            if (index == 0 || !isSameDay(bill, uiState.bills[index - 1])) {
                DateHeader(time = bill.time, modifier = Modifier.padding(bottom = 4.dp))
            }
            SwipeToDeleteContainer(onDelete = { onBillDelete(bill) }) {
                BillItem(bill, onClick = { onBillClick(bill) })
            }
        }

        item {
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun MonthHeader(time: Instant, showYear: Boolean, modifier: Modifier = Modifier) {
    val datePattern = stringResource(
        if (showYear) {
            Res.string.date_yyyy_M
        } else {
            Res.string.date_M
        }
    )
    val formatter = remember {
        DateTimeFormatter.ofPattern(datePattern)
            .withZone(ZoneId.systemDefault())
    }
    Box(
        modifier = modifier
    ) {
        Text(
            text = formatter.format(time.toJavaInstant()),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun DateHeader(time: Instant, modifier: Modifier = Modifier) {
    val datePattern = stringResource(Res.string.date_MM_dd)
    val formatter = remember {
        DateTimeFormatter.ofPattern(datePattern)
            .withZone(ZoneId.systemDefault())
    }
    Badge(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier
    ) {
        Text(
            text = formatter.format(time.toJavaInstant()),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(4.dp)
        )
    }
}

@Composable
fun BillItem(bill: Bill, onClick: () -> Unit, modifier: Modifier = Modifier) {
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
        ) {
            BillInfoSection(bill = bill, modifier = Modifier.weight(1f))
            AmountSection(amount = bill.amount, balance = bill.balance)
        }
    }
}

@Composable
private fun BillInfoSection(bill: Bill, modifier: Modifier = Modifier) {
    val tag = BillTag.ofTagId(bill.tagId)

    Column(modifier = modifier) {
        BillHeader(tag = tag, title = bill.title, time = bill.time)
        if (bill.note.isNotBlank()) {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            BillNote(note = bill.note)
        }
    }
}

@Composable
private fun BillHeader(tag: BillTag, title: String, time: Instant) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        TagIcon(tag = tag)
        Spacer(modifier = Modifier.padding(end = 8.dp))
        Column {
            Title(title)
            TimeText(time = time)
        }
    }
}
