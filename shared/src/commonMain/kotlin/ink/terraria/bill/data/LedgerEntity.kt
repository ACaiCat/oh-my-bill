package ink.terraria.bill.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ledgers")
data class LedgerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val balance: String,
)

