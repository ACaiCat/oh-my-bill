package ink.terraria.bill.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers

expect fun getBillDatabaseBuilder(): RoomDatabase.Builder<BillDatabase>

fun buildBillDatabase(): BillDatabase {
    return createBillDatabase(getBillDatabaseBuilder())
}

fun createBillDatabase(
    builder: RoomDatabase.Builder<BillDatabase>,
): BillDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
