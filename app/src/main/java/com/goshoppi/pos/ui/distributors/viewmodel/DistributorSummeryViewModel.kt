package com.goshoppi.pos.ui.distributors.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.model.local.PurchaseOrder
import timber.log.Timber
import javax.inject.Inject

class DistributorSummeryViewModel @Inject constructor(
    var distributorsRepository: DistributorsRepository

) : ViewModel() {

    private var userId = MutableLiveData<String>()

    var totalOrderObservable: LiveData<Int> = Transformations.switchMap(userId) { id ->
        distributorsRepository.getTotalOrder(id)
    }

    var totalTransactionObservable: LiveData<Int> = Transformations.switchMap(userId) { id ->
        distributorsRepository.getDisTotalTransaction(id)
    }
    var listOfOrdersObservable: LiveData<List<PurchaseOrder>> = Transformations.switchMap(userId) { id ->
        distributorsRepository.getListOfOrders(id)
    }

    fun getUserData(id: String) {
        Timber.e("getUserData")
        userId.value = id
    }


}