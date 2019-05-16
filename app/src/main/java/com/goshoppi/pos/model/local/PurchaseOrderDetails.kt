package com.goshoppi.pos.model.local

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "purchase_order_details"
)
class PurchaseOrderDetails{
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var poId: Long? = null
    var variantId: Long? = null
    var productId: Long? = null
    var productQty:Int?=null
    var mrp:String?=null
    var totalPrice:Double?=null
    var addedDate:String?=null
}