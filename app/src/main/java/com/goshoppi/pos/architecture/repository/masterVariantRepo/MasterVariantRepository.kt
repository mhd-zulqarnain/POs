package com.goshoppi.pos.architecture.repository.masterVariantRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.master.MasterVariant

interface MasterVariantRepository {

    fun loadAllMasterVariants(): LiveData<List<MasterVariant>>
    fun insertMasterVariant(variant: MasterVariant)
    fun insertMasterVariants(variants: List<MasterVariant>)
    fun searchMasterVariants(param: String): List<MasterVariant>
    suspend fun getMasterVariantsByProductId(productId: Long): List<MasterVariant>
    suspend fun getMasterStaticVariantsOfProducts(productId: Long):List<MasterVariant>
     fun getMasterStaticVariantsOfProductsWorkManager(productId: Long):List<MasterVariant>
}