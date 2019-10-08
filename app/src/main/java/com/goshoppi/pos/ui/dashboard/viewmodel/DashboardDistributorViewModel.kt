package com.goshoppi.pos.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import timber.log.Timber
import javax.inject.Inject

class DashboardDistributorViewModel@Inject constructor(
    var distributorsRepository: DistributorsRepository,
    var purchaseOrderRepository: PurchaseOrderRepository
):ViewModel(){
    private var userId = MutableLiveData<String>()
    private var poInvoiceNumber = MutableLiveData<Long>()


    var listOfPurchaseOrder : LiveData<List<PurchaseOrderDetails>> = Transformations.switchMap(poInvoiceNumber) { id ->
        purchaseOrderRepository.loadPurcahseOrderDetailByDistributorId(id)
    }
    fun getpoInvoiceNumber(id: Long) {
        poInvoiceNumber.value = id
    }
    fun getUserData(id: String) {
        userId.value = id
    }
}