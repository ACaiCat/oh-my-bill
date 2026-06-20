package ink.terraria.bill.ui.bill

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.add_bill
import bill.shared.generated.resources.amount_invalid
import bill.shared.generated.resources.amount_placeholder
import bill.shared.generated.resources.amount_required
import bill.shared.generated.resources.bill_change
import bill.shared.generated.resources.bill_note
import bill.shared.generated.resources.bill_note_hint
import bill.shared.generated.resources.bill_tag
import bill.shared.generated.resources.bill_time
import bill.shared.generated.resources.bill_time_hint
import bill.shared.generated.resources.bill_title
import bill.shared.generated.resources.bill_title_hint
import bill.shared.generated.resources.bill_title_required
import bill.shared.generated.resources.cancel
import bill.shared.generated.resources.confirm
import bill.shared.generated.resources.date_yyyy_MM_dd_HH_mm
import bill.shared.generated.resources.edit
import bill.shared.generated.resources.pick_date
import bill.shared.generated.resources.pick_time
import ink.terraria.bill.model.Bill
import ink.terraria.bill.model.BillTag
import ink.terraria.bill.model.NewBillInput
import ink.terraria.bill.model.billTags
import ink.terraria.bill.ui.IconTextButton
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBillSheet(
    onSave: (NewBillInput) -> Unit,
    onCancel: () -> Unit,
    sheetState: SheetState,
    bill: Bill? = null,
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        sheetState.expand()
    }
    ModalBottomSheet(
        onDismissRequest = onCancel,
        sheetState = sheetState
    ) {
        AddBillSheetContent(
            bill = bill,
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun AddBillSheetContent(
    onCancel: () -> Unit,
    onSave: (NewBillInput) -> Unit,
    modifier: Modifier = Modifier,
    bill: Bill? = null,
) {
    val tags = remember { billTags.values.toList() }
    var selectedIndex by remember {
        mutableIntStateOf(
            if (bill != null) tags.indexOfFirst { it.id == bill.tagId }.coerceAtLeast(0) else 0
        )
    }
    var title by remember { mutableStateOf(bill?.title ?: "") }
    var amount by remember { mutableStateOf(bill?.amount?.toString() ?: "") }
    var note by remember { mutableStateOf(bill?.note ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var titleEdited by remember { mutableStateOf(false) }
    var amountEdited by remember { mutableStateOf(false) }
    val zoneId = remember { ZoneId.systemDefault() }
    val dateTimePattern = stringResource(Res.string.date_yyyy_MM_dd_HH_mm)
    val dateTimeFormatter =
        remember(dateTimePattern) { DateTimeFormatter.ofPattern(dateTimePattern) }
    var selectedDateTime by remember {
        mutableStateOf(
            bill?.time?.toJavaInstant()?.atZone(zoneId)?.toLocalDateTime()
                ?: LocalDateTime.now().withSecond(0).withNano(0)
        )
    }
    val scrollState = rememberScrollState()
    val isTitleError = titleEdited && title.isBlank()
    val isAmountError = amountEdited && when {
        amount.isBlank() -> true
        amount.trim().toBigDecimalOrNull() == null -> true
        else -> false
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .verticalScroll(scrollState)
            .padding(
                horizontal = 24.dp,
                vertical = 16.dp
            )
    ) {
        BillFormHeader(isEdit = bill != null)
        BillTitleField(
            value = title,
            onValueChange = {
                title = it
                titleEdited = true
            },
            isError = isTitleError
        )
        BillAmountField(
            value = amount,
            onValueChange = {
                amount = it
                amountEdited = true
            },
            isError = isAmountError,
        )
        BillNoteField(
            value = note,
            onValueChange = { note = it }
        )
        FormSection(stringResource(Res.string.bill_time)) {
            DateTimeSettingItem(
                value = dateTimeFormatter.format(selectedDateTime),
                hint = stringResource(Res.string.bill_time_hint),
                onClick = { showDatePicker = true }
            )
        }
        FormSection(stringResource(Res.string.bill_tag)) {
            TagSelectionSection(
                tags = tags,
                selectedIndex = selectedIndex,
                onSelectedIndexChange = { selectedIndex = it }
            )
        }
        BillFormActionRow(
            onCancel = onCancel,
            onSave = {
                titleEdited = true
                amountEdited = true
                if (title.isNotBlank() && amount.isNotBlank() && amount.trim()
                        .toBigDecimalOrNull() != null
                ) {
                    onSave(
                        NewBillInput(
                            title = title.trim(),
                            amount = amount.trim().toBigDecimal(),
                            tagId = tags[selectedIndex].id,
                            note = note.trim(),
                            time = Instant.fromEpochMilliseconds(
                                selectedDateTime.atZone(zoneId).toInstant().toEpochMilli()
                            ),
                        )
                    )
                }
            }
        )
        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDatePicker) {
        val initialDateMillis = selectedDateTime
            .atZone(zoneId)
            .toInstant()
            .toEpochMilli()
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialDateMillis,
            initialDisplayMode = DisplayMode.Input
        )

        AlertDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis ?: initialDateMillis
                        val localDate = java.time.Instant.ofEpochMilli(selectedMillis)
                            .atZone(zoneId)
                            .toLocalDate()
                        selectedDateTime = localDate
                            .atTime(selectedDateTime.hour, selectedDateTime.minute)
                        showDatePicker = false
                        showTimePicker = true
                    }
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
            title = {
                Text(stringResource(Res.string.pick_date))
            },
            text = {
                DatePicker(
                    state = datePickerState,
                    showModeToggle = false
                )
            }
        )
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = selectedDateTime.hour,
            initialMinute = selectedDateTime.minute,
            is24Hour = true,
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedDateTime = selectedDateTime
                            .withHour(timePickerState.hour)
                            .withMinute(timePickerState.minute)
                        showTimePicker = false
                    }
                ) {
                    Text(stringResource(Res.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(Res.string.cancel))
                }
            },
            title = {
                Text(stringResource(Res.string.pick_time))
            },
            text = {
                TimeInput(state = timePickerState)
            }
        )
    }
}

@Composable
private fun BillFormHeader(isEdit: Boolean) {
    Text(
        text = stringResource(if (isEdit) Res.string.edit else Res.string.add_bill),
        style = MaterialTheme.typography.titleLarge
    )
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
private fun BillTitleField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    FormSection(
        title = stringResource(Res.string.bill_title),
        modifier = modifier,
        bottomSpacing = 8.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            label = { Text(stringResource(Res.string.bill_title)) },
            placeholder = { Text(stringResource(Res.string.bill_title_hint)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            },
            supportingText = if (isError) {
                { Text(stringResource(Res.string.bill_title_required)) }
            } else {
                null
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BillAmountField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    FormSection(
        title = stringResource(Res.string.bill_change),
        modifier = modifier,
        bottomSpacing = 8.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            isError = isError,
            label = { Text(stringResource(Res.string.bill_change)) },
            placeholder = { Text(stringResource(Res.string.amount_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.AttachMoney,
                    contentDescription = null
                )
            },
            supportingText = if (isError) {
                {
                    Text(
                        text = if (value.isBlank()) {
                            stringResource(Res.string.amount_required)
                        } else {
                            stringResource(Res.string.amount_invalid)
                        }
                    )
                }
            } else {
                null
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun BillNoteField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    FormSection(
        title = stringResource(Res.string.bill_note),
        modifier = modifier,
        bottomSpacing = 8.dp
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(stringResource(Res.string.bill_note)) },
            placeholder = { Text(stringResource(Res.string.bill_note_hint)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Notes,
                    contentDescription = null
                )
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
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

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TagSelectionSection(
    tags: List<BillTag>,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            tags.forEachIndexed { index, tag ->
                ToggleButton(
                    checked = selectedIndex == index,
                    onCheckedChange = { onSelectedIndexChange(index) },
                    shapes =
                        when (index) {
                            0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                            tags.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                            else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                        },
                    modifier = Modifier.semantics { role = Role.RadioButton },
                ) {
                    Icon(
                        imageVector = tag.icon(filled = selectedIndex == index),
                        contentDescription = stringResource(tag.nameRes),
                    )
                    Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                    Text(stringResource(tag.nameRes))
                }
            }
        }
    }
}

@Composable
private fun BillFormActionRow(
    onCancel: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
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
            onClick = onSave,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun DateTimeSettingItem(
    value: String,
    hint: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.small)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = MaterialTheme.shapes.small
            )
            .clickable(onClick = onClick)
            .padding(
                horizontal = 16.dp,
                vertical = 14.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = hint,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.size(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
