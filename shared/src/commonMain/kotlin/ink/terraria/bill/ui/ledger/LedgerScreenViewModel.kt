package ink.terraria.bill.ui.ledger

import androidx.lifecycle.ViewModel
import ink.terraria.bill.data.BillDatabase
import ink.terraria.bill.data.addLedger
import ink.terraria.bill.data.deleteLedger
import ink.terraria.bill.data.observeDomainLedgers
import ink.terraria.bill.data.updateLedger
import ink.terraria.bill.model.Ledger
import ink.terraria.bill.model.NewLedgerInput
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LedgerScreenViewModel(
    private val database: BillDatabase,
) : ViewModel() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private val _uiState = MutableStateFlow(LedgerScreenUiState())

    val uiState: StateFlow<LedgerScreenUiState> = _uiState.asStateFlow()

    init {
        scope.launch {
            database.observeDomainLedgers().collect { ledgers ->
                _uiState.value = LedgerScreenUiState(
                    isLoading = false,
                    ledgers = ledgers
                )
            }
        }
    }

    fun addLedger(input: NewLedgerInput) {
        scope.launch {
            database.addLedger(input)
        }
    }

    fun deleteLedger(ledgerId: Int) {
        scope.launch {
            database.deleteLedger(ledgerId)
        }
    }

    fun updateLedger(ledger: Ledger) {
        scope.launch {
            database.updateLedger(ledger)
        }
    }

    override fun onCleared() {
        scope.cancel()
        super.onCleared()
    }
}
