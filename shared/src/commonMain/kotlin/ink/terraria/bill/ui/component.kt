package ink.terraria.bill.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.balance
import bill.shared.generated.resources.cancel
import bill.shared.generated.resources.confirm
import bill.shared.generated.resources.confirm_delete
import bill.shared.generated.resources.delete
import bill.shared.generated.resources.delete_confirm_message
import ink.terraria.bill.model.BillTag
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Instant
import kotlin.time.toJavaInstant

@Composable
fun TagIcon(tag: BillTag, filled: Boolean = false) {
    Icon(
        imageVector = tag.icon(filled = filled),
        contentDescription = stringResource(tag.nameRes)
    )
}

@Composable
fun Title(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun TimeText(time: Instant) {
    val formatter = remember {
        DateTimeFormatter.ofPattern("HH:mm")
            .withZone(ZoneId.systemDefault())
    }

    TagText(formatter.format(time.toJavaInstant()))
}

@Composable
fun BillNote(note: String) {
    Text(
        text = note,
        style = MaterialTheme.typography.bodyMedium,
    )
}

@Composable
fun AmountSection(amount: BigDecimal, balance: BigDecimal) {
    Column(horizontalAlignment = Alignment.End) {
        AmountText(amount = amount)
        TagText("${stringResource(Res.string.balance)}${balance}")
    }
}

@Composable
fun AmountText(amount: BigDecimal, headLine: Boolean = false, showSignal: Boolean = true) {
    val isIncome = amount >= BigDecimal.ZERO
    val amountText = buildString {
        if (showSignal) {
            append(if (isIncome) "+" else "-")
        }
        append("￥")
        append(amount.abs().toString())
    }

    Text(
        text = amountText,
        style = if (headLine) MaterialTheme.typography.headlineMedium else MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = if (isIncome) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.error
        }
    )
}

@Composable
fun TagText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun IconTextButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    outlined: Boolean = false,
    enabled: Boolean = true,
) {
    val content: @Composable () -> Unit = {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    if (outlined) {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled
        ) {
            content()
        }
    } else {
        Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            content()
        }
    }
}

@Composable
fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    title: String = stringResource(Res.string.confirm_delete),
    message: String = stringResource(Res.string.delete_confirm_message)
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
        title = { Text(title) },
        text = { Text(message) }
    )
}

@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    @Suppress("DEPRECATION")
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                false
            } else {
                false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        enableDismissFromEndToStart = true,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = { content() }
    )
}

@Composable
fun EmptyList(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = "这里啥都没有捏...",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}
