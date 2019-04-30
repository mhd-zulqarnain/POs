package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.goshoppi.pos.utils.Constants

@Entity(
    tableName = "orders"
)
class Order{
    @PrimaryKey
    var orderId:Long=0
    var storeChainId:Int?=null
    var orderNum:Long?=null
    var orderDate:String?=null
    var customerId:Long?=null
    var customerName:String?=null
    var customerMobile:Long?=null
    var customerAddress:String?=null
    var orderAmount:String?=null
    var addedDate:String?=null
    var delFlag:Boolean=false
    var paymentStatus:String?=null
}