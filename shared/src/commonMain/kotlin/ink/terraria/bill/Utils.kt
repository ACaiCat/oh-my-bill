package ink.terraria.bill

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import bill.shared.generated.resources.Res
import ink.terraria.bill.model.Bill
import kotlinx.coroutines.flow.MutableStateFlow
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

val nullableIntFlowSaver = object : Saver<MutableStateFlow<Int?>, String> {
    override fun SaverScope.save(value: MutableStateFlow<Int?>): String {
        return value.value?.toString() ?: ""
    }

    override fun restore(value: String): MutableStateFlow<Int?> {
        return MutableStateFlow(value.toIntOrNull())
    }
}
