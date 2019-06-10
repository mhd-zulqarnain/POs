package com.goshoppi.pos.architecture.repository.localVariantRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalVariantDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScoped
class LocalVariantRepositoryImpl @Inject constructor(var localVariantDao: LocalVariantDao) : LocalVariantRepository {
    override fun getVariantById(id: String): LiveData<LocalVariant> {
       return localVariantDao.getVariantById(id)
    }

    override suspend fun getVaraintNameByProdId(prodId: String): String {
        return withContext(Dispatchers.IO) {
            localVariantDao.getVaraintNameByProdId(prodId)
        }
    }

    override suspend fun updateStockStatus(inStock: Boolean, varaintId: String) {
        withContext(Dispatchers.IO) {
            localVariantDao.updateStockStatus(inStock, varaintId)
        }
    }

    override suspend fun updateVarianStocktById(stock: Int, varaintId: String) {
        withContext(Dispatchers.IO) {
            localVariantDao.updateVariantById(stock, varaintId)
        }
    }

    override suspend fun getVaraintStockById(varaintId: String): Int {

        return withContext(Dispatchers.IO) { localVariantDao.getVaraintStockById(varaintId) }
    }

    override fun getVariantByBarCode(barcode: String): LiveData<LocalVariant> {
        return localVariantDao.getVaraintByBarCode(barcode)
    }

    override suspend fun loadAllStaticLocalVariants(): List<LocalVariant> {
        return withContext(Dispatchers.IO) { localVariantDao.loadAllStaticLocalVariants() }
    }

    override suspend fun deleteVaraint(storeRangeId: Long) {
        withContext(Dispatchers.IO) {
            localVariantDao.deleteVaraint(storeRangeId)
        }
    }

    override suspend fun getStaticVaraintIdList(productId: Long): List<Long> {
        return withContext(Dispatchers.IO) { localVariantDao.getVaraintIdList(productId) }
    }

    override suspend fun deleteVaraint(varaintIds: List<Long>) {
        withContext(Dispatchers.IO) { localVariantDao.deleteVaraints(varaintIds) }
    }

    override fun loadAllLocalVariants(): LiveData<List<LocalVariant>> {
        return localVariantDao.loadAllLocalVariants()
    }

    override suspend fun insertLocalVariant(variant: LocalVariant) {
        withContext(Dispatchers.IO) {
            localVariantDao.insertLocalVariant(variant)
        }
    }

    override suspend fun insertLocalVariants(variants: List<LocalVariant>) {
        withContext(Dispatchers.IO) { localVariantDao.insertLocalVariants(variants) }
    }

    override fun searchLocalVariants(param: String): List<LocalVariant> {
        return localVariantDao.getLocalVariantsSearchResult(param)
    }

    override suspend fun getLocalVariantsByProductId(productId: Long): LiveData<List<LocalVariant>> {
        return withContext(Dispatchers.IO) { localVariantDao.getLocalVariantsOfProducts(productId) }
    }
}