package ink.terraria.bill.ui.bill

import androidx.lifecycle.ViewModel
import ink.terraria.bill.data.BillDatabase
import ink.terraria.bill.data.addBill
import ink.terraria.bill.data.deleteBill
import ink.terraria.bill.data.observeDomainBills
import ink.terraria.bill.data.observeDomainLedgers
import ink.terraria.bill.data.updateBill
import ink.terraria.bill.model.Bill
import ink.terraria.bill.model.NewBillInput
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
import java.math.BigDecimal

class BillScreenViewModel(
    private val database: BillDatabase,
    private val selectedLedgerId: MutableStateFlow<Int?>,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _uiState = MutableStateFlow(BillScreenUiState())

    val uiState: StateFlow<BillScreenUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            combine(
                database.observeDomainLedgers(),
                database.observeDomainBills(),
                selectedLedgerId,
            ) { ledgers, bills, currentSelectedLedgerId ->
                val effectiveLedgerId = resolveSelectedLedgerId(ledgers, currentSelectedLedgerId)
                if (effectiveLedgerId != currentSelectedLedgerId) {
                    selectedLedgerId.value = effectiveLedgerId
                }

                val currentLedger = ledgers.find { ledger -> ledger.id == effectiveLedgerId }
                var currentBalance = currentLedger?.balance ?: BigDecimal.ZERO
                var currentBills = effectiveLedgerId?.let { ledgerId ->
                    bills.filter { it.ledgerId == ledgerId }.sortedByDescending(Bill::time)
                } ?: emptyList()
                for (i in currentBills.reversed()) {
                    currentBalance += i.amount
                    i.balance = currentBalance
                }

                BillScreenUiState(
                    isLoading = false,
                    ledgers = ledgers,
                    balance = currentBalance,
                    selectedLedgerId = effectiveLedgerId,
                    bills = currentBills,
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun selectLedger(ledgerId: Int) {
        selectedLedgerId.value = ledgerId
    }

    fun addBill(input: NewBillInput) {
        val ledgerId = uiState.value.selectedLedgerId ?: return
        scope.launch {
            database.addBill(ledgerId, input)
        }
    }

    fun deleteBill(billId: Int) {
        scope.launch {
            database.deleteBill(billId)
        }
    }

    fun updateBill(bill: Bill) {
        scope.launch {
            database.updateBill(bill)
        }
    }

    override fun onCleared() {
        scope.cancel()
    }
}
