package com.goshoppi.pos.ui.inventory.viewmodel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject

class InventoryHomeViewModel
@Inject constructor(
    var masterProductRepository: MasterProductRepository,
    var masterProductDao: MasterProductDao
) : ViewModel() {
    var productList:LiveData<PagedList<MasterProduct>>
    init{
        productList =getProdList("")
    }
    fun getProdList(param: String): LiveData<PagedList<MasterProduct>> {
        val factory = masterProductDao.loadAllPaginatedMasterSearchProduct(param)
        val config =PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setInitialLoadSizeHint(5)
            .setPageSize(5)
            .build()
        val builder = LivePagedListBuilder(factory, config)
        return builder.build()
    }
}