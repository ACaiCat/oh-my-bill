package ink.terraria.bill.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class BillEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val ledgerId: Int,
    val title: String,
    val amount: String,
    val tagId: Int,
    val note: String,
    val timeEpochMillis: Long,
)
