package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.Order

@Dao
interface  OrderDao{
    @Query("SELECT * FROM orders")
    fun loadAllOrders(): LiveData<List<Order>>

    @Query("SELECT * FROM orders")
    fun loadAllStaticOrders(): List<Order>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrder(orders: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrders(orders: List<Order>)

    @Query("SELECT * FROM orders WHERE orderNum LIKE '%' || :param || '%'")
    fun getOrdersSearchResult(param: String): List<Order>

    @Query(value = "SELECT * FROM orders WHERE customerId = :customerId")
    fun getOrdersByCustomerId(customerId: Long): LiveData<List<Order>>
    
    @Query("DELETE FROM  orders WHERE orderId =:orderId")
    fun deleteOrder(orderId:Int )

}