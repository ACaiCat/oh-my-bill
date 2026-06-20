package ink.terraria.bill.ui.ledger

import ink.terraria.bill.model.Ledger

data class LedgerScreenUiState(
    val isLoading: Boolean = true,
    val ledgers: List<Ledger> = emptyList(),
)

