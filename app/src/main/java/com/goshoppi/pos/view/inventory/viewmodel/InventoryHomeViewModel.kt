package com.goshoppi.pos.view.inventory.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject

@AppScoped
class InventoryHomeViewModel
@Inject constructor(
    var masterProductRepository: MasterProductRepository
) : ViewModel() {

    fun getProdList(param:String):LiveData<PagedList<MasterProduct>>{
        val factory = masterProductRepository.loadAllPaginatedMasterSearchProduct(param)
        val builder = LivePagedListBuilder(factory, 12)

       return  builder.build()
    }
}