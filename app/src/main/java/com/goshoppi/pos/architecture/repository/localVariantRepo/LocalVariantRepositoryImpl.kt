package com.goshoppi.pos.architecture.repository.localVariantRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalVariantDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalVaraintsWithProductName
import com.goshoppi.pos.model.local.LocalVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScoped
class LocalVariantRepositoryImpl @Inject constructor(var localVariantDao: LocalVariantDao) : LocalVariantRepository {
    override fun getVariantByBarCode(barcode: String): LiveData<LocalVaraintsWithProductName> {
     return  localVariantDao.getVaraintByBarCode(barcode)
    }

    override suspend fun loadAllStaticLocalVariants(): List<LocalVariant> {
        return withContext(Dispatchers.IO) { localVariantDao.loadAllStaticLocalVariants() }
    }

    override suspend fun deleteVaraint(storeRangeId: Int) {
        withContext(Dispatchers.IO) {
            localVariantDao.deleteVaraint(storeRangeId)
        }
    }

    override suspend fun getStaticVaraintIdList(productId: Int): List<Int> {
        return withContext(Dispatchers.IO){ localVariantDao.getVaraintIdList(productId)}
    }

    override suspend fun deleteVaraint(varaintIds: List<Int>) {
        withContext(Dispatchers.IO){ localVariantDao.deleteVaraints(varaintIds)}
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
        withContext(Dispatchers.IO){ localVariantDao.insertLocalVariants(variants)}
    }

    override fun searchLocalVariants(param: String): List<LocalVariant> {
        return localVariantDao.getLocalVariantsSearchResult(param)
    }

    override suspend fun getLocalVariantsByProductId(productId: Int): LiveData<List<LocalVariant>> {
        return withContext(Dispatchers.IO){ localVariantDao.getLocalVariantsOfProducts(productId)}
    }
}