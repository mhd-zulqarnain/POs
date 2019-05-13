package com.goshoppi.pos.view.customer.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.model.local.CreditHistory
import javax.inject.Inject

class TransactionViewModel @Inject constructor(
    var creditHistoryRepository: CreditHistoryRepository

) : ViewModel() {

    private var userId = MutableLiveData<String>()


    var listOfCreditHistoryObservable: LiveData<List<CreditHistory>> = Transformations.switchMap(userId) { id ->
        creditHistoryRepository.loadLocalAllCreditHistoryOfCustomer(id)
    }

    fun getUserData(id: String) {
        userId.value = id
    }

}