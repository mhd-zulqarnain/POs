package com.goshoppi.pos.architecture.repository.localProductRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.StoreCategory
import com.goshoppi.pos.model.SubCategory
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant

interface LocalProductRepository {

    fun loadAllLocalProduct(): List<LocalProduct>
    fun loadAllLiveLocalProduct(): LiveData<List<LocalProduct>>
    suspend fun loadAllStaticLocalProduct(): List<LocalProduct>
    suspend fun loadAllWeightedPrd(): List<LocalProduct>
    suspend fun insertLocalProduct(product: LocalProduct)
    suspend fun insertLocalProducts(productList: List<LocalProduct>)
    fun insertStaticLocalProducts(productList: List<LocalProduct>)

    fun searchLocalProducts(param: String): LiveData<List<LocalProduct>>
    suspend fun deleteLocalProducts(id: Long)
    fun getProductByBarCode(barcode: String): LiveData<LocalProduct>
    suspend fun getProductNameById(product_id: Long): String
    suspend fun isProductExist(product_id: Long): String?
    fun insertStoreCategory(storeCategory: StoreCategory)
    fun insertSubCategory(subCategory: SubCategory)

    suspend fun loadAllWeightedBySubcategoryId(id: String): List<LocalProduct>
    fun loadAllWeightedVaraintByProductId(id: String): LiveData<List<LocalVariant>>
    suspend fun loadSubCategory(): List<SubCategory>
    suspend fun loadSubCategoryByCategoryId(categoryId: Long): List<SubCategory>
    suspend fun loadSubCategoryNameByCategoryId(categoryId: Long): String

    fun insertStoreCategories(storeCategories: List<StoreCategory>)
    suspend fun loadStoreCategory(): List<StoreCategory>
    fun getMasterStaticVariantsOfProductsWorkManager(productId: Long): List<LocalVariant>

}