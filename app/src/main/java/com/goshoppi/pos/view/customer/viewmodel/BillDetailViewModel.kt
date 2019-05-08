package com.goshoppi.pos.view.customer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import javax.inject.Inject

class BillDetailViewModel @Inject constructor(
    var customerRepository: CustomerRepository

) : ViewModel() {

    private var userId = MutableLiveData<String>()
    private var orderId = MutableLiveData<String>()

    var totalOrderObservable: LiveData<Int> = Transformations.switchMap(userId) { id ->
            customerRepository.getTotalOrder(id)
    }

    var totalTransactionObservable: LiveData<Int> = Transformations.switchMap(userId) { id ->
            customerRepository.getTotalTransaction(id)
    }
    var listOfOrdersObservable: LiveData<List<Order>> = Transformations.switchMap(userId) { id ->
            customerRepository.getListOfOrders(id)
    }

    var listOfOrderItemObservable: LiveData<List<OrderItem>> = Transformations.switchMap(orderId) { id ->
            customerRepository.getListOfOrderItem(id)
    }

    fun getOrderData(id: String) {
        userId.value = id
    }
    fun getOrderItemData(id: String) {
        orderId.value = id
    }

}