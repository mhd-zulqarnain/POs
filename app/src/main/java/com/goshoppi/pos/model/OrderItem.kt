package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_item")
class OrderItem{
    @PrimaryKey(autoGenerate = true)
    var orderitemId:Int=0
    var orderId:Long?=null
    var productId:Long?=null
    var productQty:Int?=null
    var mrp:String?=null
    var totalPrice:Double?=null
    var taxAmount:Double?=null
    var addedDate:String?=null
    var  productName:String?=null
    var delFlag:Boolean=false
    var variantId:Long?=null


}
