package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_item")
class OrderItem{
    @PrimaryKey(autoGenerate = true)
    var orderitemId:Int=0
    var orderId:Int?=null
    var productId:Long?=null
    var productQty:Long?=null
    var mrp:String?=null
    var totalPrice:Double?=null
    var taxAmount:Double?=null
    var addedDate:String?=null
    var delFlag:Boolean=false

}
