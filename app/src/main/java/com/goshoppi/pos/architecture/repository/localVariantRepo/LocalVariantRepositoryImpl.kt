package com.goshoppi.pos.architecture.repository.localVariantRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalVariantDao
import com.goshoppi.pos.model.local.LocalVariant
import javax.inject.Inject

class LocalVariantRepositoryImpl @Inject constructor(var localVariantDao: LocalVariantDao) : LocalVariantRepository {

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

    override fun getLocalVariantsByProductId(productId: String): LiveData<List<LocalVariant>> {
        return localVariantDao.getLocalVariantsOfProducts(productId)
    }
}