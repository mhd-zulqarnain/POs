package com.goshoppi.pos.architecture.repository.customerRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalCustomerDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject


class CustomerRepositoryImp @Inject constructor(private var customerDao: LocalCustomerDao) : CustomerRepository {
        override suspend fun loadAllLocalCustomerByDate(
        upperLimit: Date,
        lowerLimit: Date
    ):List<LocalCustomer> {
        return withContext(Dispatchers.IO) {
            customerDao.loadAllLocalCustomerByDate(upperLimit, lowerLimit)
        }}

    override suspend fun loadNumberofCustomer(): Long {
        return  customerDao.loadNumberofCustomer()
    }

    override fun filterListOfOrdersByRange(
        customerId: String,
        upperLimit: String,
        lowerLimit: String
    ): LiveData<List<Order>> {
        return customerDao.filterListOfOrdersByRange(customerId,upperLimit,lowerLimit)
    }

    override suspend fun loadAllStaticLocalCustomer(): List<LocalCustomer> {
        return withContext(Dispatchers.IO) { customerDao.loadLocalAllStaticCustomer() }
    }

    override suspend fun getTotalDebit(): LiveData<Double> {
        return withContext(Dispatchers.IO) { customerDao.getTotalDebit() }
    }

    override suspend fun getCustomerStaticCredit(customerId: String): Double {
        return withContext(Dispatchers.IO) { customerDao.getCustomerStaticCredit(customerId) }
    }

    override suspend fun updateCredit(customerId: String,credit: Double, date: Date) {
        withContext(Dispatchers.IO) {
         customerDao.updateCredit(customerId,credit,date)
        }
    }

    override fun getListOfOrderItem(orderId: String): LiveData<List<OrderItem>> {
        return customerDao.getListOfOrderItem(orderId)
    }

    override suspend fun getCustomerCredit(customerId: String): LiveData<Double> {
        return withContext(Dispatchers.IO) { customerDao.getCustomerCredit(customerId) }
    }

    override fun getTotalOrder(customerId: String): LiveData<Int> {
        return customerDao.getTotalOrder(customerId)
    }

    override fun getTotalTransaction(customerId: String): LiveData<Int> {
        return customerDao.getTotalTransaction(customerId)
    }

    override fun getListOfOrders(customerId: String): LiveData<List<Order>> {
        return customerDao.getListOfOrders(customerId)
    }

    override suspend fun searchLocalStaticCustomers(param: String): List<LocalCustomer> {
        return withContext(Dispatchers.IO) {
            customerDao.getLocalSearchStaticResult(param)
        }
    }

    override fun loadAllLocalCustomer(): LiveData<List<LocalCustomer>> {
        return customerDao.loadLocalAllCustomer()
    }

    override suspend fun insertLocalCustomer(customer: LocalCustomer) {
        withContext(Dispatchers.IO) {
            customerDao.insertLocalCustomer(customer)
        }
    }

    override fun insertLocalCustomers(customerList: List<LocalCustomer>) {
        customerDao.insertLocalCustomers(customerList)
    }

    override fun searchLocalCustomers(param: String): LiveData<List<LocalCustomer>> {
        return customerDao.getLocalSearchResult(param)
    }

    override suspend fun deleteLocalCustomers(phoneId: Long) {
        withContext(Dispatchers.IO) { customerDao.deleteLocalCustomers(phoneId) }
    }
}