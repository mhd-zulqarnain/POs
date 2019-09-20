package com.goshoppi.pos.ui.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

class DashboardViewModel @Inject constructor(
    var cutomerRepository: CustomerRepository
):ViewModel() {

    val viewModelJob = Job()
    val viewModelScope = CoroutineScope(Dispatchers.Main + viewModelJob)


}