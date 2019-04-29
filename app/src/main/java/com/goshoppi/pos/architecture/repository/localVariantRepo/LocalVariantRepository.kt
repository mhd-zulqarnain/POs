package com.goshoppi.pos.architecture.repository.localVariantRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.LocalVaraintsWithProductName
import com.goshoppi.pos.model.local.LocalVariant

interface LocalVariantRepository {
    fun loadAllLocalVariants(): LiveData<List<LocalVariant>>
    suspend fun loadAllStaticLocalVariants(): List<LocalVariant>
    suspend fun insertLocalVariant(variant: LocalVariant)
    suspend fun insertLocalVariants(variants: List<LocalVariant>)
    fun searchLocalVariants(param: String): List<LocalVariant>
    suspend fun getLocalVariantsByProductId(productId: Int): LiveData<List<LocalVariant>>
    suspend fun deleteVaraint(varaintIds: List<Int>)
    suspend fun getStaticVaraintIdList(productId: Int): List<Int>
    suspend fun deleteVaraint(storeRangeId: Int)
    fun getVariantByBarCode(barcode: String): LiveData<LocalVaraintsWithProductName>
}