package com.goshoppi.pos.architecture.repository.customerRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalCustomerDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.local.LocalCustomer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScoped
class CustomerRepositoryImp @Inject constructor(private var customerDao: LocalCustomerDao) : CustomerRepository {
    override fun getTotalOrder(customerId: String): LiveData<Int> {
        return customerDao.getTotalOrder(customerId)
    }

    override fun getTotalTransaction(customerId: String): LiveData<Int> {
        return customerDao.getTotalTransaction(customerId)
    }

    override fun getListOfOrders(customerId: String): LiveData<List<Order>> {
        return customerDao.getListOfOrders(customerId)
    }

    suspend override fun searchLocalStaticCustomers(param: String): List<LocalCustomer> {
        return withContext(Dispatchers.IO) {
            customerDao.getLocalSearchStaticResult(param)
        }
    }

    override fun loadAllLocalCustomer(): LiveData<List<LocalCustomer>> {
        return customerDao.loadLocalAllCustomer()
    }

    suspend override fun insertLocalCustomer(customer: LocalCustomer) {
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
        withContext(Dispatchers.IO) { customerDao.deleteLocalCustomers(phoneId)}
    }
}