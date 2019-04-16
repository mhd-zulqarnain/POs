package com.goshoppi.pos.architecture.repository.localVariantRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalVariantDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.local.LocalVariant
import javax.inject.Inject

@AppScoped
class LocalVariantRepositoryImpl @Inject constructor(var localVariantDao: LocalVariantDao) : LocalVariantRepository {
    override fun deleteVaraint(storeRangeId: Int) {
        localVariantDao.deleteVaraint(storeRangeId)
    }

    override fun getVaraintIdList(productId: Int): LiveData<List<Int>> {
        return  localVariantDao.getVaraintIdList(productId)
    }

    override fun deleteVaraint(varaintIds:List<Int> ) {
        localVariantDao.deleteVaraints(varaintIds)
    }

    override fun loadAllLocalVariants(): LiveData<List<LocalVariant>> {
        return localVariantDao.loadAllLocalVariants()
    }

    override fun insertLocalVariant(variant: LocalVariant) {
        localVariantDao.insertLocalVariant(variant)
    }

    override fun insertLocalVariants(variants: List<LocalVariant>) {
        localVariantDao.insertLocalVariants(variants)
    }

    override fun searchLocalVariants(param: String): List<LocalVariant> {
        return localVariantDao.getLocalVariantsSearchResult(param)
    }

    override fun getLocalVariantsByProductId(productId: Int): LiveData<List<LocalVariant>> {
        return localVariantDao.getLocalVariantsOfProducts(productId)
    }
}