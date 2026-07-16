package ink.terraria.bill.ui.bill

import ink.terraria.bill.model.Bill
import ink.terraria.bill.model.Ledger
import java.math.BigDecimal

data class BillScreenUiState(
    val isLoading: Boolean = true,
    val ledgers: List<Ledger> = emptyList(),
    val selectedLedgerId: Int? = null,
    val balance: BigDecimal = BigDecimal.ZERO,
    val bills: List<Bill> = emptyList(),
)
