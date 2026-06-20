package ink.terraria.bill.data

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

lateinit var appContext: Context

fun init(context: Context) {
    appContext = context
}

actual fun getBillDatabaseBuilder(): RoomDatabase.Builder<BillDatabase> {
    val dbFile = appContext.getDatabasePath("bill.db")
    return Room.databaseBuilder<BillDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    ).fallbackToDestructiveMigration(false)
}
