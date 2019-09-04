package com.goshoppi.pos.architecture.repository.customerRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer

interface CustomerRepository {
    fun loadAllLocalCustomer(): LiveData<List<LocalCustomer>>
    suspend  fun loadAllStaticLocalCustomer(): List<LocalCustomer>
    suspend  fun loadNumberofCustomer(): Long
    suspend fun insertLocalCustomer(customer: LocalCustomer)
    fun insertLocalCustomers(customerList: List<LocalCustomer>)
    fun searchLocalCustomers(param: String): LiveData<List<LocalCustomer>>
    suspend fun searchLocalStaticCustomers(param: String): List<LocalCustomer>
    suspend fun deleteLocalCustomers(phoneId: Long)
    fun getTotalOrder(customerId: String): LiveData<Int>
    fun getTotalTransaction(customerId: String): LiveData<Int>
    fun getListOfOrders(customerId: String): LiveData<List<Order>>
    fun filterListOfOrdersByRange(customerId: String,
                                  upperLimit:String ,
                                  lowerLimit:String ): LiveData<List<Order>>
    suspend fun getCustomerCredit(customerId: String): LiveData<Double>
    suspend fun getCustomerStaticCredit(customerId: String):Double
    suspend  fun updateCredit(customerId: String,credit:Double,date:String)
    suspend   fun getTotalDebit(): LiveData<Double>
    fun getListOfOrderItem(orderId: String): LiveData<List<OrderItem>>

}
