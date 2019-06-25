package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "orders"
)
class Order{
    @PrimaryKey
    var orderId:Long=0
    var storeChainId:Int?=null
    var orderNum:Long?=null
    var orderDate:Long?=null  //android timestamp
    var customerId:Long?=null
    var discount:String?=null
    var customerName:String?=null
    var customerMobile:Long?=null
    var customerAddress:String?=null
    var orderAmount:String?=null
    var addedDate:String?=null
    var delFlag:Boolean=false
    var paymentStatus:String?=null
}