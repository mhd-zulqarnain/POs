package com.goshoppi.pos.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull


@Entity(
    tableName = "credit_history"
)
class CreditHistory {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var customerId: Long? = null
    var orderId: Long? = null
    var creditAmount: Double ? = null
    var paidAmount: Double ? = null
    var totalCreditAmount: Double ? = null
    var transcationDate :String ? = null

}