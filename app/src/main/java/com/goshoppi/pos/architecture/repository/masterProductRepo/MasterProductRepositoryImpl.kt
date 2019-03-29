package com.goshoppi.pos.architecture.repository.masterProductRepo

import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject
import javax.inject.Singleton
import android.arch.paging.PagedList.Config.Builder
import java.util.concurrent.Executors

@Singleton
class MasterProductRepositoryImpl @Inject constructor( var masterProductDao: MasterProductDao):MasterProductRepository {
    override fun loadAllPaginatedMasterSearchProduct(param: String): LiveData<PagedList<MasterProduct>> {
        val PAGE_SIZE = 7
        val config = Builder()
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()
        val factory =masterProductDao.loadAllPaginatedMasterSearchProduct(param)
        return  LivePagedListBuilder(factory, config)
            .setInitialLoadKey(1)
            .build()

    }

    override fun loadAllStaticMasterProduct(): List<MasterProduct> {
        return masterProductDao.loadAllStaticProduct()
    }

    override fun loadAllMasterProduct(): LiveData<List<MasterProduct>> {
        return masterProductDao.loadAllProduct()

    }

    override fun insertMasterProduct(product: MasterProduct) {
        masterProductDao.insertProduct(product)
    }

    override fun insertMasterProducts(productList: List<MasterProduct>) {
        masterProductDao.insertProducts(productList)
    }

    override fun searchMasterProducts(param: String): LiveData<List<MasterProduct>> {
        return  masterProductDao.getSearchResult(param)
    }


}