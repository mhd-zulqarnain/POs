package com.goshoppi.pos.architecture.repository.localVariantRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.LocalVariant

interface LocalVariantRepository {
    fun loadAllLocalVariants(): LiveData<List<LocalVariant>>
    suspend fun loadAllStaticLocalVariants(): List<LocalVariant>
    suspend fun insertLocalVariant(variant: LocalVariant)
    suspend fun insertLocalVariants(variants: List<LocalVariant>)
    fun searchLocalVariants(param: String): List<LocalVariant>
    suspend fun getLocalVariantsByProductId(productId: Long): LiveData<List<LocalVariant>>
    suspend fun deleteVaraint(varaintIds: List<Long>)
    suspend fun getStaticVaraintIdList(productId: Long): List<Long>
    suspend fun deleteVaraint(storeRangeId: Long)
    fun getVariantByBarCode(barcode: String): LiveData<LocalVariant>
    fun getVariantById(id: String): LiveData<LocalVariant>
    suspend fun getVaraintStockById(varaintId: String): Int
    suspend fun updateStockStatus(inStock: Boolean, varaintId: String)
    suspend fun updateVarianStocktById(stock: Int, varaintId: String)
    suspend fun getVaraintNameByProdId(prodId:String): String


}