package com.goshoppi.pos.architecture.repository.orderRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.OrderDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.Order
import javax.inject.Inject

@AppScoped
class OrderRepositoryImp @Inject constructor(private var orderDao: OrderDao):OrderRepository {
    override fun loadAllOrders(): LiveData<List<Order>> {
        return  orderDao.loadAllOrders()
    }

    override fun loadAllStaticOrders(): List<Order> {
        return  orderDao.loadAllStaticOrders()
    }

    override fun insertOrders(orders: List<Order>) {
          orderDao.insertOrders(orders)
    }

    override fun insertOrder(order: Order) {
        orderDao.insertOrder(order)}

    override fun getOrdersSearchResult(param: String): List<Order> {
        return  orderDao.getOrdersSearchResult(param)
    }



    override fun getOrdersByCustomerId(customerId: Long): LiveData<List<Order>> {
        return  orderDao.getOrdersByCustomerId(customerId)
    }

    override fun deleteOrder(orderId: Int) {
        orderDao.deleteOrder(orderId)
    }

}