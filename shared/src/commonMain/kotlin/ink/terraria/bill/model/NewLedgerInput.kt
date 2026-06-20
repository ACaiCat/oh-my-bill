package ink.terraria.bill.model

import java.math.BigDecimal

data class NewLedgerInput(
    val name: String,
    val balance: BigDecimal,
)

