package com.goshoppi.pos.architecture.repository.orderItemRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.OrderItemDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.OrderItem
import javax.inject.Inject

@AppScoped
class OrderItemRepositoryImp @Inject constructor(private var orderItemDao: OrderItemDao):OrderItemRepository{
    override fun loadAllOrderItems(): LiveData<List<OrderItem>> {
       return orderItemDao.loadAllOrderItems()
    }

    override fun insertOrderItem(orderItem: OrderItem) {
        orderItemDao.insertOrderItem(orderItem)
    }

    override fun insertOrderItems(orderItems: List<OrderItem>) {
        orderItemDao.insertOrderItems(orderItems)
    }

    override fun getOrderItemsOfOrders(orderId: Int): LiveData<List<OrderItem>> {
        return  orderItemDao.getOrderItemsOfOrders(orderId)
    }

}