package com.goshoppi.pos.view.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.model.Flag
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import com.goshoppi.pos.model.master.ReceiveOrderItem
import kotlinx.coroutines.*
import javax.inject.Inject

class ReceiveInventoryViewModel @Inject constructor(
    var localVariantRepository: LocalVariantRepository,
    var distributorsRepository: DistributorsRepository
) : ViewModel() {

    var flag: MutableLiveData<Flag> = MutableLiveData()
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var productBarCode: MutableLiveData<String> = MutableLiveData()
    var searchNameParam: MutableLiveData<String> = MutableLiveData()
    var holdedCount: MutableLiveData<String> = MutableLiveData()
    var distributor: Distributor ?= null
    var poDetailList: ArrayList<PurchaseOrderDetails> = ArrayList()
    var subtotal = 0.00
    var orderId: Long = System.currentTimeMillis()

    var productObservable: LiveData<LocalVariant> = Transformations.switchMap(productBarCode) { barcode ->
        localVariantRepository.getVariantByBarCode(barcode)

    }

    var cutomerListObservable: LiveData<List<Distributor>> = Transformations.switchMap(searchNameParam) { name ->
        distributorsRepository.searchDistributors(name)

    }
    fun addDistributor(distributor: Distributor) {
        uiScope.launch {
            distributorsRepository.insertDistributor(distributor)
        }
    }
    fun search(barcode: String) {
        productBarCode.value = barcode

    }

    fun searchCustomer(name: String) {
        searchNameParam.value = name

    }

    fun setFlag(obj: Flag) {
        flag.value = obj

    }
    /** LocalInventoryActivity Local Product Variants Work END*/
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}