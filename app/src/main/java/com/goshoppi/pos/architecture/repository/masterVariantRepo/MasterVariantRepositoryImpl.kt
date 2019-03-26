package com.goshoppi.pos.architecture.repository.masterVariantRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.MasterVariantDao
import com.goshoppi.pos.model.master.MasterVariant
import javax.inject.Inject

class MasterVariantRepositoryImpl @Inject constructor(private var masterVariantDao: MasterVariantDao) :
    MasterVariantRepository {

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

    override fun getMasterVariantsByProductId(productId: String): LiveData<List<MasterVariant>> {
        return masterVariantDao.getMasterVariantsOfProducts(productId)
    }

    override fun getMasterStaticVariantsOfProducts(productId: String): List<MasterVariant> {
        return masterVariantDao.getMasterStaticVariantsOfProducts(productId)
    }
}