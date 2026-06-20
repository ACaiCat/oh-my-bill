package ink.terraria.bill.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.math.BigDecimal

data class DayBill(
    val day: Int,
    var amount: BigDecimal,
    var balance: BigDecimal
) {
    companion object {
        fun ofMonthBills(bills: Collection<Bill>): List<DayBill> {
            require(bills.isNotEmpty()) { "账单不能为空" }

            val sorted = bills.sortedBy { it.time }
            val dates = sorted.map { it.time.toLocalDateTime(TimeZone.currentSystemDefault()) }
            require(dates.first().month == dates.last().month) { "账单范围超过一个月" }

            return sorted
                .groupBy { it.time.toLocalDateTime(TimeZone.currentSystemDefault()).day }
                .map { (day, dayBills) ->
                    DayBill(
                        day = day,
                        amount = dayBills.fold(BigDecimal.ZERO) { acc, bill -> acc + bill.amount },
                        balance = dayBills.last().balance
                    )
                }
                .sortedBy { it.day }
        }
    }
}

