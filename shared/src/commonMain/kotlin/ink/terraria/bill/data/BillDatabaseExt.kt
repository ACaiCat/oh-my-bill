package ink.terraria.bill.data

import ink.terraria.bill.model.Bill
import ink.terraria.bill.model.Ledger
import ink.terraria.bill.model.NewBillInput
import ink.terraria.bill.model.NewLedgerInput
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Instant

fun BillDatabase.observeDomainLedgers(): Flow<List<Ledger>> {
    return billStoreDao().observeLedgers().map { ledgers -> ledgers.map(LedgerEntity::toModel) }
}

fun BillDatabase.observeDomainBills(): Flow<List<Bill>> {
    return billStoreDao().observeBills().map { bills -> bills.map(BillEntity::toModel) }
}

suspend fun BillDatabase.addBill(
    ledgerId: Int,
    input: NewBillInput,
) {
    billStoreDao().addBill(ledgerId, input)
}

suspend fun BillDatabase.addLedger(
    input: NewLedgerInput,
) {
    billStoreDao().addLedger(input)
}

suspend fun BillDatabase.deleteBill(id: Int) {
    billStoreDao().deleteBillById(id)
}

suspend fun BillDatabase.deleteLedger(id: Int) {
    billStoreDao().deleteLedgerAndBills(id)
}

suspend fun BillDatabase.updateBill(bill: Bill) {
    billStoreDao().updateBill(bill.toEntity())
}

suspend fun BillDatabase.updateLedger(ledger: Ledger) {
    billStoreDao().updateLedger(ledger.toEntity())
}

private fun LedgerEntity.toModel(): Ledger {
    return Ledger(
        id = id,
        name = name,
        balance = balance.toBigDecimal(),
    )
}

private fun BillEntity.toModel(): Bill {
    return Bill(
        id = id,
        ledgerId = ledgerId,
        title = title,
        amount = amount.toBigDecimal(),
        tagId = tagId,
        note = note,
        time = Instant.fromEpochMilliseconds(timeEpochMillis),
    )
}

private fun Ledger.toEntity(): LedgerEntity {
    return LedgerEntity(
        id = id,
        name = name,
        balance = balance.toPlainString(),
    )
}

private fun Bill.toEntity(): BillEntity {
    return BillEntity(
        id = id,
        ledgerId = ledgerId,
        title = title,
        amount = amount.toPlainString(),
        tagId = tagId,
        note = note,
        timeEpochMillis = time.toEpochMilliseconds(),
    )
}
