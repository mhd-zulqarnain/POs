package com.goshoppi.pos.view.customer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.local.Distributor
import javax.inject.Inject

class SummeryViewModel @Inject constructor(
    var customerRepository: CustomerRepository,
    var distributorsRepository: DistributorsRepository


) : ViewModel() {

    private var userId = MutableLiveData<String>()

    var totalOrderObservable: LiveData<Int> = Transformations.switchMap(userId) { id ->
            customerRepository.getTotalOrder(id)
    }

    var totalTransactionObservable: LiveData<Int> = Transformations.switchMap(userId) { id ->
            customerRepository.getTotalTransaction(id)
    }
    var listOfOrdersObservable: LiveData<List<Order>> = Transformations.switchMap(userId) { id ->
            customerRepository.getListOfOrders(id)
    }
   /* var listOfdisObservable: LiveData<List<Distributor>> = Transformations.switchMap(userId) { id ->
        distributorsRepository.loadAllDistributor()
    }*/


    fun getUserData(id: String) {
        userId.value = id
    }

}