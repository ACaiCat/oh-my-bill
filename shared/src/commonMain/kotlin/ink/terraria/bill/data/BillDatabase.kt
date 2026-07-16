@file:Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")

package ink.terraria.bill.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(
    entities = [LedgerEntity::class, BillEntity::class],
    version = 3,
    exportSchema = true,
)

@ConstructedBy(BillDatabaseConstructor::class)
abstract class BillDatabase : RoomDatabase() {
    abstract fun billStoreDao(): BillStoreDao
}

expect object BillDatabaseConstructor : RoomDatabaseConstructor<BillDatabase> {
    override fun initialize(): BillDatabase
}
