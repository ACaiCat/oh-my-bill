package ink.terraria.bill.ui.statistic

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import bill.shared.generated.resources.Res
import bill.shared.generated.resources.a_bill
import bill.shared.generated.resources.app_name
import bill.shared.generated.resources.month_selector_format
import bill.shared.generated.resources.month_selector_next_month
import bill.shared.generated.resources.month_selector_previous_month
import bill.shared.generated.resources.statistic_balance
import bill.shared.generated.resources.statistic_expenditure_format
import bill.shared.generated.resources.statistic_expense_category
import bill.shared.generated.resources.statistic_income_format
import bill.shared.generated.resources.statistic_overview
import ink.terraria.bill.model.BillTag
import ink.terraria.bill.model.TagBill
import ink.terraria.bill.ui.AmountText
import ink.terraria.bill.ui.BillAppBar
import ink.terraria.bill.ui.EmptyList
import ink.terraria.bill.ui.TagText
import ink.terraria.bill.ui.Title
import ink.terraria.bill.ui.navigation.NavigationDestination
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.number
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.math.BigDecimal
import java.math.RoundingMode

object StatisticDestination : NavigationDestination {
    override val route: String = "statistic"
    override val titleRes: StringResource = Res.string.app_name
}


@Composable
fun StatisticScreen(
    uiState: StatisticScreenUiState,
    onSelectLedgerClick: (Int) -> Unit,
    onSelectMonthClick: (LocalDate) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            BillAppBar(
                ledgers = uiState.ledgers,
                selectLedgerId = uiState.selectedLedgerId,
                onSelectLedgerClick = onSelectLedgerClick,
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(Res.string.statistic_overview),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(4.dp)
                    )

                    MonthSelector(
                        selectedMonth = uiState.selectedMonth,
                        onSelectMonth = onSelectMonthClick
                    )
                }
            }

            item {
                StatisticCard(income = uiState.income, expenditure = uiState.expenditure)
            }

            item {
                Text(
                    text = stringResource(Res.string.statistic_expense_category),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(4.dp)
                )

                if (uiState.tagBills.isEmpty()) {
                    EmptyList(modifier = Modifier.fillParentMaxHeight(0.6f))
                }
            }

            items(uiState.tagBills) { bill ->
                TagBillItem(bill)
            }
        }
    }
}

@Composable
fun StatisticCard(income: BigDecimal, expenditure: BigDecimal, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            AmountText(income - expenditure, headLine = true)
            TagText(stringResource(Res.string.statistic_balance))
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Row {
                Text(
                    text = stringResource(Res.string.statistic_income_format, income.setScale(2, RoundingMode.HALF_DOWN).toString()),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.padding(end = 16.dp))
                Text(
                    text = stringResource(
                        Res.string.statistic_expenditure_format,
                        expenditure.setScale(2, RoundingMode.HALF_DOWN).toString()
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
fun MonthSelector(
    selectedMonth: LocalDate,
    onSelectMonth: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = {
            val prevMonth = if (selectedMonth.month == Month.JANUARY) {
                LocalDate(selectedMonth.year - 1, 12, 1)
            } else {
                LocalDate(selectedMonth.year, selectedMonth.month.number - 1, 1)
            }
            onSelectMonth(prevMonth)
        }) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = stringResource(Res.string.month_selector_previous_month)
            )
        }

        Text(
            text = stringResource(
                Res.string.month_selector_format,
                selectedMonth.year,
                selectedMonth.month.number
            ),
            style = MaterialTheme.typography.bodyLarge
        )

        IconButton(onClick = {
            val nextMonth = if (selectedMonth.month.number == 12) {
                LocalDate(selectedMonth.year + 1, 1, 1)
            } else {
                LocalDate(selectedMonth.year, selectedMonth.month.number + 1, 1)
            }
            onSelectMonth(nextMonth)
        }) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.month_selector_next_month)
            )
        }
    }
}

@Preview
@Composable
fun StatisticCardPreview() {
    StatisticCard(999.toBigDecimal(), 233.toBigDecimal())
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TagBillItem(tagBill: TagBill, modifier: Modifier = Modifier) {
    val tag = BillTag.ofTagId(tagBill.tagId)
    var proportion by remember { mutableFloatStateOf(0f) }
    val animatedProgress by animateFloatAsState(
        targetValue = proportion,
        animationSpec = tween(
            durationMillis = 3000,
            easing = FastOutSlowInEasing
        ),
    )

    LaunchedEffect(tagBill.tagId) {
        proportion = tagBill.proportion
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp).fillMaxWidth()
        ) {
            Icon(
                imageVector = tag.icon(true),
                contentDescription = stringResource(tag.nameRes)
            )
            Spacer(modifier = Modifier.padding(end = 8.dp))

            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.width(200.dp)) {
                Title(stringResource(tag.nameRes))
                Spacer(modifier = Modifier.padding(top = 8.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.24f)
                )
            }

            AmountSection(
                amount = tagBill.amount,
                count = tagBill.count,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun AmountSection(amount: BigDecimal, count: Int, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.End, modifier = modifier) {
        AmountText(amount, showSignal = false)
        TagText(count.toString() + stringResource(Res.string.a_bill))
    }
}

@Composable
@Preview
fun TagBillItemPreview() {
    TagBillItem(
        tagBill = TagBill(
            1, BigDecimal(100), 3, 0.2f
        )
    )
}
