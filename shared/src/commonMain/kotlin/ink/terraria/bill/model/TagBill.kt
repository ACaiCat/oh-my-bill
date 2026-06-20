package ink.terraria.bill.model

import java.math.BigDecimal
import java.math.RoundingMode

data class TagBill(
    val tagId: Int,
    var amount: BigDecimal,
    var count: Int,
    var proportion: Float
) {
    companion object {
        fun ofBills(bills: Collection<Bill>): List<TagBill> {
            val totalAmount = bills.fold(BigDecimal.ZERO) { acc, bill -> acc + bill.amount }
            return bills
                .groupBy { it.tagId }
                .map { (tagId, tagBills) ->
                    val tagAmount =
                        tagBills.fold(BigDecimal.ZERO) { acc, bill -> acc + bill.amount }
                    TagBill(
                        tagId = tagId,
                        amount = tagAmount,
                        count = tagBills.size,
                        proportion = if (totalAmount.compareTo(BigDecimal.ZERO) != 0) {
                            tagAmount.divide(totalAmount, 4, RoundingMode.HALF_UP).toFloat()
                        } else 0f
                    )
                }
        }
    }
}
