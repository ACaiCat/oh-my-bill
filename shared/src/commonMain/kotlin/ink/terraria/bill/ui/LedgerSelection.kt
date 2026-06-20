package ink.terraria.bill.ui

import ink.terraria.bill.model.Ledger

internal fun resolveSelectedLedgerId(
    ledgers: List<Ledger>,
    currentSelectedLedgerId: Int?,
): Int? {
    return when {
        ledgers.isEmpty() -> null
        currentSelectedLedgerId != null && ledgers.any { it.id == currentSelectedLedgerId } -> {
            currentSelectedLedgerId
        }

        else -> ledgers.first().id
    }
}
