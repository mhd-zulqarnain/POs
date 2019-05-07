package com.goshoppi.pos.architecture.repository.localProductRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalProductDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScoped
class LocalProductRepositoryImpl @Inject constructor(var localProductDao: LocalProductDao) : LocalProductRepository {
    override suspend fun isProductExist(product_id: Int): String? {
        return withContext(Dispatchers.IO) {
            localProductDao.isProductExist(product_id)
        }
    }

    override suspend fun getProductNameById(product_id: Int): String {
        return withContext(Dispatchers.IO) { localProductDao.getProductNameById(product_id) }
    }

    override suspend fun loadAllStaticLocalProduct(): List<LocalProduct> {
        return withContext(Dispatchers.IO) {
            localProductDao.loadAllStaticLocalProduct()
        }

    }

    override fun getProductByBarCode(barcode: String): LiveData<LocalProduct> {
        return localProductDao.getProductByBarCode(barcode)
    }

    override suspend fun deleteLocalProducts(id: Int) {
        withContext(Dispatchers.IO) { localProductDao.deleteLocalProducts(id) }

    }

    override fun loadAllLocalProduct(): LiveData<List<LocalProduct>> {
        return localProductDao.loadLocalAllProduct()
    }

    override suspend fun insertLocalProduct(product: LocalProduct) {
        withContext(Dispatchers.IO) {
            localProductDao.insertLocalProduct(product)
        }
    }

    override suspend fun insertLocalProducts(productList: List<LocalProduct>) {
        withContext(Dispatchers.IO) {
            localProductDao.insertLocalProducts(productList)
        }
    }

    override fun searchLocalProducts(param: String): LiveData<List<LocalProduct>> {
        return localProductDao.getLocalSearchResult(param)
    }


}