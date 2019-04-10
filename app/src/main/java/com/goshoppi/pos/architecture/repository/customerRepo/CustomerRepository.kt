package com.goshoppi.pos.architecture.repository.customerRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.model.local. LocalCustomer

interface CustomerRepository {
    fun loadAllLocalCustomer(): LiveData<List< LocalCustomer>>
    fun insertLocalCustomer(customer: LocalCustomer)
    fun insertLocalCustomers(customerList:List<LocalCustomer> )
    fun searchLocalCustomers(param: String): LiveData<List<LocalCustomer>>
    fun searchLocalStaticCustomers(param: String): List<LocalCustomer>
    fun deleteLocalCustomers(phoneId: Int)
}