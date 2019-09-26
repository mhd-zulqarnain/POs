package com.goshoppi.pos.model.master

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "receive_order_item")
class ReceiveOrderItem{
    @PrimaryKey(autoGenerate = true)
    var receiveOrderitemId:Int=0
    var receiveOrderId:Long?=null
    var productId:Long?=null
    var productQty:Int?=null
    var mrp:String?=null
    var totalPrice:Double?=null
    var taxAmount:Double?=null
    var addedDate: Date?=null
    var delFlag:Boolean=false
    var variantId:Int?=null
}
