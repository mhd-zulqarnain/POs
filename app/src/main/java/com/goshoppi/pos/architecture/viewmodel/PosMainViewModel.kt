package com.goshoppi.pos.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalProduct
import javax.inject.Inject

@AppScoped
class PosMainViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository
) : ViewModel() {

    val productBarCode: MutableLiveData<String> = MutableLiveData()

    val items: LiveData<LocalProduct> = Transformations.switchMap(productBarCode) { barcode ->
        localProductRepository.getProductByBarCode(barcode)
    }

    init {
        productBarCode.value = ""
    }

    fun search(barcode: String) {
        productBarCode.value = barcode
    }
}
