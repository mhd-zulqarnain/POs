package com.goshoppi.pos.architecture.repository.orderRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.Order

interface OrderRepository{

    fun loadAllOrders(): LiveData<List<Order>>
    fun loadAllStaticOrders(): List<Order>
    fun insertOrders(orders: List<Order>)
    fun insertOrder(order: Order)
    fun getOrdersSearchResult(param: String): List<Order>
    fun getOrdersByCustomerId(customerId: Long) :LiveData<List<Order>>
    fun deleteOrder(orderId:Int )
}