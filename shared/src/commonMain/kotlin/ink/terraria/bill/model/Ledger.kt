package ink.terraria.bill.model

import java.math.BigDecimal

data class Ledger(
    val id: Int,
    val name: String,
    val balance: BigDecimal,
)
