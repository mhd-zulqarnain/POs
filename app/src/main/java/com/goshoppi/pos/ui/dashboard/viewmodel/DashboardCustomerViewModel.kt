package com.goshoppi.pos.ui.dashboard.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import timber.log.Timber
import javax.inject.Inject

class DashboardCustomerViewModel@Inject constructor(
    var customerRepository: CustomerRepository,
    var purchaseOrderRepository: PurchaseOrderRepository
):ViewModel(){
    var listOfCustomer: LiveData<List<LocalCustomer>> = customerRepository.loadAllLocalCustomer()

}