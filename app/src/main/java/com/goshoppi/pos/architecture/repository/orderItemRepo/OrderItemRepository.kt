package com.goshoppi.pos.architecture.repository.orderItemRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.OrderItem

interface OrderItemRepository {


    fun loadAllOrderItems(): LiveData<List<OrderItem>>
    fun insertOrderItem(orderItem: OrderItem)
    suspend fun insertOrderItems(orderItems: List<OrderItem>)
    fun getOrderItemsOfOrders(orderId: Int):LiveData<List<OrderItem>>
}