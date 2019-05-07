package com.goshoppi.pos.architecture.repository.localProductRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.LocalProduct

interface LocalProductRepository {

    fun loadAllLocalProduct(): LiveData<List<LocalProduct>>
    suspend fun loadAllStaticLocalProduct(): List<LocalProduct>
    suspend fun insertLocalProduct(product: LocalProduct)
    suspend fun insertLocalProducts(productList: List<LocalProduct>)
    fun searchLocalProducts(param: String): LiveData<List<LocalProduct>>
    suspend fun deleteLocalProducts(id: Int)
    fun getProductByBarCode(barcode: String): LiveData<LocalProduct>
    suspend fun getProductNameById(product_id: Int): String
    suspend fun isProductExist(product_id: Int): String?

}