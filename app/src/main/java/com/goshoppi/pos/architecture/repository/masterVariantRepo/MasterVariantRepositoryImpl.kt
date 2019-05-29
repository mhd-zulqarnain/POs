package com.goshoppi.pos.architecture.repository.masterVariantRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.MasterVariantDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.master.MasterVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScoped
class MasterVariantRepositoryImpl @Inject constructor(private var masterVariantDao: MasterVariantDao) :
    MasterVariantRepository {
    override fun getMasterStaticVariantsOfProductsWorkManager(productId: Long): List<MasterVariant> {
        return  masterVariantDao.getMasterStaticVariantsOfProducts(productId)
    }

    override fun loadAllMasterVariants(): LiveData<List<MasterVariant>> {
        return masterVariantDao.loadAllMasterVariants()
    }

    override fun insertMasterVariant(variant: MasterVariant) {
        masterVariantDao.insertMasterVariant(variant)
    }

    override fun insertMasterVariants(variants: List<MasterVariant>) {
        masterVariantDao.insertMasterVariants(variants)
    }

    override fun searchMasterVariants(param: String): List<MasterVariant> {
        return masterVariantDao.getMasterVariantsSearchResult(param)
    }

    override suspend fun getMasterVariantsByProductId(productId: Long): List<MasterVariant> {
        return withContext(Dispatchers.IO){ masterVariantDao.getMasterVariantsOfProducts(productId)}
    }

    override suspend  fun getMasterStaticVariantsOfProducts(productId: Long): List<MasterVariant> {
        return withContext(Dispatchers.IO){
            masterVariantDao.getMasterStaticVariantsOfProducts(productId)
    }
    }
}