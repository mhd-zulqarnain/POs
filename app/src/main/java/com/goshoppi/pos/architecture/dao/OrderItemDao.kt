package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.OrderItem

@Dao
interface  OrderItemDao{

    @Query("SELECT * FROM order_item")
    fun loadAllOrderItems(): LiveData<List<OrderItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderItem(variants: OrderItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrderItems(variantsList: List<OrderItem>)

    @Query(value = "SELECT * FROM order_item WHERE orderId = :orderId")
    fun getOrderItemsOfOrders(orderId: Int): LiveData<List<OrderItem>>

}