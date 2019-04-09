package com.goshoppi.pos.architecture.repository.customerRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalCustomerDao
import com.goshoppi.pos.model.local.LocalCustomer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepositoryImp @Inject constructor(var customerDao: LocalCustomerDao):CustomerRepository{
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

    override fun deleteLocalCustomers(phone: Int) {
        customerDao.deleteLocalCustomers(phone)
    }
}