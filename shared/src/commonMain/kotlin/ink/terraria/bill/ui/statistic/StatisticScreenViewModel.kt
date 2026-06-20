package ink.terraria.bill.ui.statistic

import androidx.lifecycle.ViewModel
import ink.terraria.bill.data.BillDatabase
import ink.terraria.bill.data.observeDomainBills
import ink.terraria.bill.data.observeDomainLedgers
import ink.terraria.bill.model.TagBill
import ink.terraria.bill.ui.resolveSelectedLedgerId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal
import kotlin.time.Clock

class StatisticScreenViewModel(
    private val database: BillDatabase,
    private val selectedLedgerId: MutableStateFlow<Int?>,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val selectedMonth = MutableStateFlow(
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.let {
            LocalDate(
                it.year,
                it.month,
                1
            )
        }
    )
    private val _uiState = MutableStateFlow(StatisticScreenUiState())

    val uiState: StateFlow<StatisticScreenUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                database.observeDomainLedgers(),
                database.observeDomainBills(),
                selectedLedgerId,
                selectedMonth,
            ) { ledgers, bills, currentSelectedLedgerId, currentSelectedMonth ->
                val effectiveLedgerId = resolveSelectedLedgerId(ledgers, currentSelectedLedgerId)
                if (effectiveLedgerId != currentSelectedLedgerId) {
                    selectedLedgerId.value = effectiveLedgerId
                }

                val selectedBills = effectiveLedgerId?.let { ledgerId ->
                    bills.filter {
                        it.ledgerId == ledgerId &&
                                it.time.toLocalDateTime(TimeZone.currentSystemDefault())
                                    .let { dateTime ->
                                        dateTime.year == currentSelectedMonth.year && dateTime.month == currentSelectedMonth.month
                                    }
                    }
                } ?: emptyList()
                val expenseBills = selectedBills.filter { it.amount < BigDecimal.ZERO }

                StatisticScreenUiState(
                    isLoading = false,
                    ledgers = ledgers,
                    selectedLedgerId = effectiveLedgerId,
                    selectedMonth = currentSelectedMonth,
                    income = selectedBills
                        .filter { it.amount > BigDecimal.ZERO }
                        .fold(BigDecimal.ZERO) { total, bill -> total + bill.amount },
                    expenditure = expenseBills
                        .fold(BigDecimal.ZERO) { total, bill -> total + bill.amount.abs() },
                    tagBills = TagBill.ofBills(expenseBills).sortedBy { it.amount },
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectLedger(ledgerId: Int) {
        selectedLedgerId.value = ledgerId
    }

    fun selectMonth(month: LocalDate) {
        selectedMonth.value = month
    }

    override fun onCleared() {
        scope.cancel()
    }
}
