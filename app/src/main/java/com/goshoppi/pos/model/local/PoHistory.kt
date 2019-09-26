package com.goshoppi.pos.model.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*


@Entity(
        tableName = "po_history"
)
class PoHistory {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var distributorId: Long? = null
    var poId: Long? = null
    var creditAmount: Double ? = null
    var paidAmount: Double ? = null
    var transcationDate : Date? = null

}