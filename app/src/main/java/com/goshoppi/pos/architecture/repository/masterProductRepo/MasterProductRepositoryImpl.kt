package com.goshoppi.pos.architecture.repository.masterProductRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.MasterProductDao
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MasterProductRepositoryImpl @Inject constructor( var masterProductDao: MasterProductDao):MasterProductRepository {
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