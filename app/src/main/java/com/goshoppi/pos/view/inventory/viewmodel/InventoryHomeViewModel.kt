package com.goshoppi.pos.view.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.master.MasterProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import javax.inject.Inject

class InventoryHomeViewModel
@Inject constructor(
    var masterProductRepository: MasterProductRepository
) : ViewModel() {

    fun getProdList(param: String): LiveData<PagedList<MasterProduct>> {
        val factory = masterProductRepository.loadAllPaginatedMasterSearchProduct(param)
        val builder = LivePagedListBuilder(factory, 15)
        return builder.build()
    }
}