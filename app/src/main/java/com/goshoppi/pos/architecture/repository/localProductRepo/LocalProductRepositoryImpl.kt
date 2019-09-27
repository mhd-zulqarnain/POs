package com.goshoppi.pos.architecture.repository.localProductRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.LocalProductDao
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.StoreCategory
import com.goshoppi.pos.model.SubCategory
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class LocalProductRepositoryImpl @Inject constructor(private var localProductDao: LocalProductDao) :
    LocalProductRepository {
    override suspend fun getSalesByDay(day: Date): List<OrderItem> {
        return withContext(Dispatchers.IO) {
            localProductDao.getSalesByDay(day)
        }}

    override suspend fun getNumberOfSalesByDay(day: Date): Double {
        return withContext(Dispatchers.IO) {
            localProductDao.getNumberOfSalesByDay(day)
        }
    }

    override suspend fun loadStoreCategoryMain(): List<StoreCategory> {
        return withContext(Dispatchers.IO) {
            localProductDao.loadStoreCategoryMain()

        }
    }

    override suspend fun insertSubCategoryMain(subCategory: SubCategory) {
        withContext(Dispatchers.IO) {
            localProductDao.insertSubCategory(subCategory)


        }
    }

    override suspend fun insertStoreCategoryMain(storeCategory: StoreCategory) {
        withContext(Dispatchers.IO) {
            localProductDao.insertStoreCategory(storeCategory)

        }
    }

    override suspend fun loadSubCategoryNameByCategoryId(categoryId: Long): String {
        return withContext(Dispatchers.IO) {
            localProductDao.loadSubCategoryNameByCategoryId(categoryId)
        }
    }

    override fun loadAllLiveLocalProduct(): LiveData<List<LocalProduct>> {
        return localProductDao.loadLocalliveAllProduct()
    }

    override fun getMasterStaticVariantsOfProductsWorkManager(productId: Long): List<LocalVariant> {
        return localProductDao.getMasterStaticVariantsOfProducts(productId)
    }

    override fun insertStaticLocalProducts(productList: List<LocalProduct>) {
        localProductDao.insertLocalProducts(productList)
    }


    override fun loadAllWeightedVaraintByProductId(id: String): LiveData<List<LocalVariant>> {
        return localProductDao.loadAllWeightedVaraintByProductId(id)
    }

    override suspend fun loadAllWeightedBySubcategoryId(id: String): List<LocalProduct> {
        return withContext(Dispatchers.IO) {
            localProductDao.loadAllWeightedBySubcategoryId(id)
        }
    }


    override suspend fun loadAllWeightedPrd(): List<LocalProduct> {
        return withContext(Dispatchers.IO) {
            localProductDao.loadAllWeightedPrd()
        }
    }

    override fun loadStoreCategory(): LiveData<List<StoreCategory>> {
        return localProductDao.loadStoreCategory()
    }

    override suspend fun isProductExist(product_id: Long): String? {
        return withContext(Dispatchers.IO) {
            localProductDao.isProductExist(product_id)
        }
    }

    override suspend fun getProductNameById(product_id: Long): String {
        return withContext(Dispatchers.IO) { localProductDao.getProductNameById(product_id) }
    }

    override suspend fun loadAllStaticLocalProduct(): List<LocalProduct> {
        return withContext(Dispatchers.IO) {
            localProductDao.loadAllStaticLocalProduct()
        }

    }

    override fun getProductByBarCode(barcode: String): LiveData<LocalProduct> {
        return localProductDao.getProductByBarCode(barcode)
    }

    override suspend fun deleteLocalProducts(id: Long) {
        withContext(Dispatchers.IO) { localProductDao.deleteLocalProducts(id) }

    }

    override fun loadAllLocalProduct(): List<LocalProduct> {
        return localProductDao.loadLocalAllProduct()
    }

    override suspend fun insertLocalProduct(product: LocalProduct) {
        withContext(Dispatchers.IO) {
            localProductDao.insertLocalProduct(product)
        }
    }

    override suspend fun insertLocalProducts(productList: List<LocalProduct>) {
        withContext(Dispatchers.IO) {
            localProductDao.insertLocalProducts(productList)
        }
    }

    override fun searchLocalProducts(param: String): LiveData<List<LocalProduct>> {
        return localProductDao.getLocalSearchResult(param)
    }

    override fun insertStoreCategory(storeCategory: StoreCategory) {
        localProductDao.insertStoreCategory(storeCategory)
    }

    override fun insertSubCategory(subCategory: SubCategory) {
        localProductDao.insertSubCategory(subCategory)
    }

    override fun loadSubCategory(): LiveData<List<SubCategory>> {
        return localProductDao.loadSubCategory()
    }

    override suspend fun loadSubCategoryByCategoryId(categoryId: Long): List<SubCategory> {
        return withContext(Dispatchers.IO) {
            localProductDao.loadSubCategoryByCategoryId(categoryId)
        }
    }

    override fun insertStoreCategories(storeCategories: List<StoreCategory>) {
        localProductDao.insertStoreCategories(storeCategories)
    }

}