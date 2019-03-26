package com.goshoppi.pos.architecture.repository.localVariantRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.model.local.LocalVariant

interface LocalVariantRepository {
    fun loadAllLocalVariants(): LiveData<List<LocalVariant>>
    fun insertLocalVariant(variant: LocalVariant)
    fun insertLocalVariants(variants: List<LocalVariant>)
    fun searchLocalVariants(param: String): List<LocalVariant>
    fun getLocalVariantsByProductId(productId: String): LiveData<List<LocalVariant>>
}