package com.goshoppi.pos.view.distributors.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.model.local.PoHistory
import javax.inject.Inject



class DistributorTransactionViewModel @Inject constructor(
    var purchaseOrderRepository: PurchaseOrderRepository

) : ViewModel() {

    private var userId = MutableLiveData<String>()


    var listOfCreditHistoryObservable: LiveData<List<PoHistory>> = Transformations.switchMap(userId) { id ->
        purchaseOrderRepository.loadLocalAllCreditHistory(id)
    }

    fun getUserData(id: String) {
        userId.value = id
    }

}

