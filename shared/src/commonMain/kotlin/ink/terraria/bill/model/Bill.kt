package ink.terraria.bill.model

import java.math.BigDecimal
import kotlin.time.Clock
import kotlin.time.Instant

data class Bill(
    val id: Int,
    val ledgerId: Int,
    val title: String,
    val amount: BigDecimal,
    val balance: BigDecimal,
    val tagId: Int,
    val note: String,
    val time: Instant = Clock.System.now()
)
