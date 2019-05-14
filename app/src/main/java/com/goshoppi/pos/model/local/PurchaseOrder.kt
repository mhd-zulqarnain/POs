package com.goshoppi.pos.model.local

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_order"
)
class PurchaseOrder{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var poNumber: Long? = null
    var poInvoiceNumber: Long? = null
    var paid: Double  = 0.00
    var paymentType :String ? = null
    var poDate :String ? = null
    var totalAmount: Long ?= null
}