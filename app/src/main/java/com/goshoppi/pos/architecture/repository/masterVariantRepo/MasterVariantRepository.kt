package com.goshoppi.pos.architecture.repository.masterVariantRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.model.master.MasterVariant

interface MasterVariantRepository {

    fun loadAllMasterVariants(): LiveData<List<MasterVariant>>
    fun insertMasterVariant(variant: MasterVariant)
    fun insertMasterVariants(variants: List<MasterVariant>)
    fun searchMasterVariants(param: String): List<MasterVariant>
    fun getMasterVariantsByProductId(productId: Int): LiveData<List<MasterVariant>>
    fun getMasterStaticVariantsOfProducts(productId: Int):List<MasterVariant>
}