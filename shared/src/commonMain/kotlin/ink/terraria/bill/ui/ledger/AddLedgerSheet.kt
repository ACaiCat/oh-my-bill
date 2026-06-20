package ink.terraria.bill.ui.ledger

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.add_ledger
import bill.shared.generated.resources.amount_invalid
import bill.shared.generated.resources.amount_required
import bill.shared.generated.resources.balance
import bill.shared.generated.resources.cancel
import bill.shared.generated.resources.confirm
import bill.shared.generated.resources.edit_ledger
import bill.shared.generated.resources.initial_balance
import bill.shared.generated.resources.initial_balance_hint
import bill.shared.generated.resources.ledger_name
import bill.shared.generated.resources.ledger_name_hint
import bill.shared.generated.resources.ledger_name_required
import ink.terraria.bill.model.Ledger
import ink.terraria.bill.model.NewLedgerInput
import ink.terraria.bill.ui.IconTextButton
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLedgerSheet(
    onSave: (NewLedgerInput) -> Unit,
    onCancel: () -> Unit,
    sheetState: SheetState,
    ledger: Ledger? = null,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        sheetState.expand()
    }
    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState
    ) {
        AddLedgerSheetContent(
            ledger = ledger,
            onCancel = {
                scope.launch {
                    sheetState.hide()
                    onCancel()
                }
            },
            onSave = { input ->
                scope.launch {
                    sheetState.hide()
                    onSave(input)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddLedgerSheetContent(
    onCancel: () -> Unit,
    onSave: (NewLedgerInput) -> Unit,
    modifier: Modifier = Modifier,
    ledger: Ledger? = null
) {
    var name by remember { mutableStateOf(ledger?.name ?: "") }
    var balance by remember { mutableStateOf(ledger?.balance?.toString() ?: "") }
    var nameEdited by remember { mutableStateOf(false) }
    var balanceEdited by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val isNameError = nameEdited && name.isBlank()
    val isBalanceError = balanceEdited && when {
        balance.isBlank() -> true
        balance.trim().toBigDecimalOrNull() == null -> true
        else -> false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(if (ledger != null) Res.string.edit_ledger else Res.string.add_ledger),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        FormSection(title = stringResource(Res.string.ledger_name)) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameEdited = true
                },
                isError = isNameError,
                label = { Text(stringResource(Res.string.ledger_name)) },
                placeholder = { Text(stringResource(Res.string.ledger_name_hint)) },
                leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                supportingText = if (isNameError) {
                    { Text(stringResource(Res.string.ledger_name_required)) }
                } else null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        FormSection(title = stringResource(Res.string.initial_balance)) {
            OutlinedTextField(
                value = balance,
                onValueChange = {
                    balance = it
                    balanceEdited = true
                },
                isError = isBalanceError,
                label = { Text(stringResource(Res.string.balance)) },
                placeholder = { Text(stringResource(Res.string.initial_balance_hint)) },
                leadingIcon = { Icon(Icons.Outlined.AttachMoney, contentDescription = null) },
                supportingText = if (isBalanceError) {
                    {
                        Text(
                            text = if (balance.isBlank()) stringResource(Res.string.amount_required)
                            else stringResource(Res.string.amount_invalid)
                        )
                    }
                } else null,
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            IconTextButton(
                text = stringResource(Res.string.cancel),
                icon = Icons.Default.Close,
                onClick = onCancel,
                modifier = Modifier.weight(1f),
                outlined = true
            )
            IconTextButton(
                text = stringResource(Res.string.confirm),
                icon = Icons.Default.Check,
                onClick = {
                    onSave(
                        NewLedgerInput(
                            name, balance.toBigDecimal()
                        )
                    )
                },
                modifier = Modifier.weight(1f),
                enabled = name.isNotBlank() && balance.trim().toBigDecimalOrNull() != null
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun FormSection(
    title: String,
    modifier: Modifier = Modifier,
    bottomSpacing: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        content()
        Spacer(modifier = Modifier.height(bottomSpacing))
    }
}
