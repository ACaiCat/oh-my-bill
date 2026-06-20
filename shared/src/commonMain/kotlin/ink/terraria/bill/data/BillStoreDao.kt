package ink.terraria.bill.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import ink.terraria.bill.model.NewBillInput
import ink.terraria.bill.model.NewLedgerInput
import kotlinx.coroutines.flow.Flow

@Dao
interface BillStoreDao {
    @Query("SELECT * FROM ledgers ORDER BY id")
    fun observeLedgers(): Flow<List<LedgerEntity>>

    @Query("SELECT * FROM bills ORDER BY timeEpochMillis")
    fun observeBills(): Flow<List<BillEntity>>

    @Query("SELECT * FROM ledgers WHERE id = :id LIMIT 1")
    suspend fun getLedgerById(id: Int): LedgerEntity?

    @Query("SELECT COUNT(*) FROM ledgers")
    suspend fun countLedgers(): Int

    @Query("SELECT COUNT(*) FROM bills")
    suspend fun countBills(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLedgers(ledgers: List<LedgerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBills(bills: List<BillEntity>)

    @Insert
    suspend fun insertBill(bill: BillEntity)

    @Insert
    suspend fun insertLedger(ledger: LedgerEntity)

    @Update
    suspend fun updateBill(bill: BillEntity)

    @Update
    suspend fun updateLedger(ledger: LedgerEntity)

    @Query("DELETE FROM bills WHERE id = :id")
    suspend fun deleteBillById(id: Int)

    @Query("DELETE FROM ledgers WHERE id = :id")
    suspend fun deleteLedgerById(id: Int)

    @Transaction
    suspend fun deleteLedgerAndBills(id: Int) {
        deleteBillByLedgerId(id)
        deleteLedgerById(id)
    }

    @Query("DELETE FROM bills WHERE ledgerId = :ledgerId")
    suspend fun deleteBillByLedgerId(ledgerId: Int)

    @Transaction
    suspend fun seedIfNeeded(
        ledgers: List<LedgerEntity>,
        bills: List<BillEntity>,
    ) {
        if (countLedgers() != 0 || countBills() != 0) {
            return
        }
        insertLedgers(ledgers)
        insertBills(bills)
    }

    @Transaction
    suspend fun addBill(
        ledgerId: Int,
        input: NewBillInput,
    ) {
        val ledger = getLedgerById(ledgerId) ?: error("Ledger $ledgerId does not exist")
        val nextBalance = ledger.balance.toBigDecimal() + input.amount
        insertBill(
            BillEntity(
                ledgerId = ledgerId,
                title = input.title,
                amount = input.amount.toPlainString(),
                balance = nextBalance.toPlainString(),
                tagId = input.tagId,
                note = input.note,
                timeEpochMillis = input.time.toEpochMilliseconds(),
            )
        )
        updateLedger(ledger.copy(balance = nextBalance.toPlainString()))
    }

    suspend fun addLedger(input: NewLedgerInput) {
        insertLedger(
            LedgerEntity(
                id = 0,
                name = input.name,
                balance = input.balance.toPlainString(),
            )
        )
    }
}
