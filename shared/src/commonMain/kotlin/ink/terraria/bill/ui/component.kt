package ink.terraria.bill.ui

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.balance
import bill.shared.generated.resources.bottom_tip
import bill.shared.generated.resources.cancel
import bill.shared.generated.resources.confirm
import bill.shared.generated.resources.confirm_delete
import bill.shared.generated.resources.delete
import bill.shared.generated.resources.delete_confirm_message
import bill.shared.generated.resources.empty
import ink.terraria.bill.model.BillTag
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
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
        TagText("${stringResource(Res.string.balance)} ${balance.setScale(2, RoundingMode.HALF_DOWN)}")
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
        append(amount.abs().setScale(2, RoundingMode.HALF_DOWN).toString())
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

enum class DragAnchor { Default, Revealed }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToDeleteContainer(
    onDelete: () -> Unit,
    content: @Composable () -> Unit
) {
    val dragState = remember {
        AnchoredDraggableState(
            initialValue = DragAnchor.Default
        )
    }

    val flingBehavior = AnchoredDraggableDefaults.flingBehavior(
        state = dragState,
        positionalThreshold = { distance: Float -> distance * 0.4f },
        animationSpec = tween(durationMillis = 200)
    )

    LaunchedEffect(dragState.settledValue) {
        if (dragState.settledValue == DragAnchor.Revealed) {
            onDelete()
            dragState.animateTo(DragAnchor.Default)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { size ->
                val maxDrag = -size.width.toFloat() * 0.4f
                dragState.updateAnchors(
                    DraggableAnchors {
                        DragAnchor.Default at 0f
                        DragAnchor.Revealed at maxDrag
                    }
                )
            }
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    MaterialTheme.colorScheme.errorContainer
                ).padding(horizontal = 20.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(Res.string.delete),
                tint = if (dragState.currentValue == DragAnchor.Revealed)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onErrorContainer
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .offset {
                    val offsetPx = dragState.offset
                    IntOffset(
                        x = if (offsetPx.isNaN()) 0 else offsetPx.roundToInt(),
                        y = 0
                    )
                }
                .anchoredDraggable(
                    state = dragState,
                    orientation = Orientation.Horizontal,
                    flingBehavior = flingBehavior
                )
        ) {
            content()
        }
    }
}

@Composable
fun EmptyList(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        Text(
            text = stringResource(Res.string.empty),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .align(Alignment.Center)
        )
    }
}

@Composable
fun BottomEndTip(
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = stringResource(Res.string.bottom_tip),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.padding(vertical = 24.dp))
    }
}
