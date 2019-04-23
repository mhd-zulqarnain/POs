package com.goshoppi.pos.architecture.viewmodel

import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.scope.AppScoped
import timber.log.Timber
import javax.inject.Inject

@AppScoped
class ProductViewModel @Inject constructor(
   var localProductRepository: LocalProductRepository,
   var localCustomerRepository: CustomerRepository?,
   var masterProductRepository: MasterProductRepository
) : ViewModel() {

    init {
        Timber.e("ProductViewModel Init")

    }

    fun showTags(){
        //Timber.e("localProductRepository ${localProductRepository.loadAllLocalProduct().value!!.size}")
        Timber.e("localCustomerRepository ${if (localCustomerRepository != null){Timber.e("localCustomerRepository If")} else{Timber.e("localCustomerRepository else")} } ")
        Timber.e("masterProductRepository ${masterProductRepository.loadAllStaticMasterProduct().size }")
    }
}
