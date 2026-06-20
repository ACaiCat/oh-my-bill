package ink.terraria.bill.model

import java.math.BigDecimal
import kotlin.time.Instant

data class NewBillInput(
    val title: String,
    val amount: BigDecimal,
    val tagId: Int,
    val note: String,
    val time: Instant,
)
