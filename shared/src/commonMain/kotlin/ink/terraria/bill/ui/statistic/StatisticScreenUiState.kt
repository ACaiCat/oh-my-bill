package ink.terraria.bill.ui.statistic

import ink.terraria.bill.model.Ledger
import ink.terraria.bill.model.TagBill
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal
import kotlin.time.Clock

data class StatisticScreenUiState(
    val isLoading: Boolean = true,
    val ledgers: List<Ledger> = emptyList(),
    val selectedLedgerId: Int? = null,
    val selectedMonth: LocalDate = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).date.let {
        LocalDate(
            it.year,
            it.month,
            1
        )
    },
    val income: BigDecimal = BigDecimal.ZERO,
    val expenditure: BigDecimal = BigDecimal.ZERO,
    val tagBills: List<TagBill> = emptyList(),
)
