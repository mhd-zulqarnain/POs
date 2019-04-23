package com.goshoppi.pos.architecture.repository.customerRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalCustomerDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalCustomer
import javax.inject.Inject
import javax.inject.Singleton

@AppScoped
class CustomerRepositoryImp @Inject constructor(private var customerDao: LocalCustomerDao):CustomerRepository{
    override fun searchLocalStaticCustomers(param: String): List<LocalCustomer> {
        return  customerDao.getLocalSearchStaticResult(param)
    }

    override fun loadAllLocalCustomer(): LiveData<List<LocalCustomer>> {
        return customerDao.loadLocalAllCustomer()
    }

    override fun insertLocalCustomer(customer: LocalCustomer) {
        customerDao.insertLocalCustomer(customer)
    }

    override fun insertLocalCustomers(customerList: List<LocalCustomer>) {
        customerDao.insertLocalCustomers(customerList)
    }

    override fun searchLocalCustomers(param: String): LiveData<List<LocalCustomer>> {
        return  customerDao.getLocalSearchResult(param)
    }

    override fun deleteLocalCustomers(phoneId: Long) {
        customerDao.deleteLocalCustomers(phoneId)
    }
}