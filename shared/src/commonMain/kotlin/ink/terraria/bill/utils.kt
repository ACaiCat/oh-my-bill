package ink.terraria.bill

import bill.shared.generated.resources.Res
import bill.shared.generated.resources.icon
import ink.terraria.bill.model.Bill
import java.time.Year
import java.time.YearMonth
import java.time.ZoneId
import kotlin.time.toJavaInstant

fun isSameDay(bill: Bill, bill2: Bill): Boolean {
    val zone = ZoneId.systemDefault()
    return bill.time.toJavaInstant().atZone(zone).toLocalDate() ==
            bill2.time.toJavaInstant().atZone(zone).toLocalDate()
}

fun isSameMonth(bill: Bill, bill2: Bill): Boolean {
    val zone = ZoneId.systemDefault()
    return YearMonth.from(
        bill.time.toJavaInstant().atZone(zone)
    ) == YearMonth.from(bill2.time.toJavaInstant().atZone(zone))
}

fun isSameYear(bill: Bill, bill2: Bill): Boolean {
    val zone = ZoneId.systemDefault()
    return Year.from(
        bill.time.toJavaInstant().atZone(zone)
    ) == Year.from(bill2.time.toJavaInstant().atZone(zone))
}

val iconRes = Res.drawable.icon
