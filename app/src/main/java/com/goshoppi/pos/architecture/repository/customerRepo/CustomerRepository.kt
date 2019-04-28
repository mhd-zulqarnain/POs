package com.goshoppi.pos.architecture.repository.customerRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.local.LocalCustomer

interface CustomerRepository {
    fun loadAllLocalCustomer(): LiveData<List<LocalCustomer>>
    suspend fun insertLocalCustomer(customer: LocalCustomer)
    fun insertLocalCustomers(customerList: List<LocalCustomer>)
    fun searchLocalCustomers(param: String): LiveData<List<LocalCustomer>>
    suspend fun searchLocalStaticCustomers(param: String): List<LocalCustomer>
    suspend fun deleteLocalCustomers(phoneId: Long)
    fun getTotalOrder(customerId:String): LiveData<Int>
     fun getTotalTransaction(customerId:String): LiveData<Int>
     fun getListOfOrders(customerId:String): LiveData<List<Order>>
}