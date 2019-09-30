package com.goshoppi.pos.architecture.repository.masterProductRepo

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject
import javax.inject.Singleton
import androidx.paging.PagedList.Config.Builder
import com.goshoppi.pos.di2.scope.AppScoped

@AppScoped
class MasterProductRepositoryImpl @Inject constructor( var masterProductDao: MasterProductDao):MasterProductRepository {
    override fun loadAllPaginatedMasterSearchProduct(param: String): DataSource.Factory<Int, MasterProduct> {

        return  masterProductDao.loadAllPaginatedMasterSearchProduct(param)

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