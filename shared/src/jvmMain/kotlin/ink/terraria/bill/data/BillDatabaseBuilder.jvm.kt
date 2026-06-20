package ink.terraria.bill.data

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual fun getBillDatabaseBuilder(): RoomDatabase.Builder<BillDatabase> {
    val dbDir = File(System.getProperty("user.home"), ".bill")
    dbDir.mkdirs()
    val dbFile = File(dbDir, "bill.db")
    return Room.databaseBuilder<BillDatabase>(
        name = dbFile.absolutePath,
    ).fallbackToDestructiveMigration(false)
}
