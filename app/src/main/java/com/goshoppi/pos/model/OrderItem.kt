package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goshoppi.pos.utils.Constants
import java.util.*

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
    var addedDate: Date?=null
    var  productName:String?=null
    var delFlag:Boolean=false
    var variantId:Long?=null
    @Transient   //skip this filed to add in database and avoid to add it in forming json
    var type: Int ?=null

}
