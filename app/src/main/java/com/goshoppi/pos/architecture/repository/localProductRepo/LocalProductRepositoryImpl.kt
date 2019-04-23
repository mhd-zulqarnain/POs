package com.goshoppi.pos.architecture.repository.localProductRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalProductDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalProduct
import javax.inject.Inject
import javax.inject.Singleton

@AppScoped
class LocalProductRepositoryImpl @Inject constructor(var localProductDao: LocalProductDao):LocalProductRepository {
    override fun loadAllStaticLocalProduct(): List<LocalProduct> {
        return localProductDao.loadAllStaticLocalProduct()
    }

    override fun getProductByBarCode(barcode: String): LocalProduct? {
        return localProductDao.getProductByBarCode(barcode)
    }

    override fun deleteLocalProducts(id: Int) {
        return localProductDao.deleteLocalProducts(id)

    }

    override fun loadAllLocalProduct(): LiveData<List<LocalProduct>> {
        return localProductDao.loadLocalAllProduct()
    }

    override fun insertLocalProduct(product: LocalProduct) {
        localProductDao.insertLocalProduct(product)
    }

    override fun insertLocalProducts(productList: List<LocalProduct>) {
        localProductDao.insertLocalProducts(productList)
    }

    override fun searchLocalProducts(param: String): LiveData<List<LocalProduct>> {
        return  localProductDao.getLocalSearchResult(param)
    }


}