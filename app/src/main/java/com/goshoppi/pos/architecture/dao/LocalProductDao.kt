package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.StoreCategory
import com.goshoppi.pos.model.SubCategory
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import java.util.*


@Dao
interface LocalProductDao {

    @Query("SELECT * FROM local_products")
    fun loadLocalAllProduct():List<LocalProduct>
    @Query("SELECT * FROM local_products")
    fun loadLocalliveAllProduct():LiveData<List<LocalProduct>>

    @Query("SELECT * FROM local_products")
    fun loadAllStaticLocalProduct(): List<LocalProduct>

    @Query("SELECT * FROM local_products")
    fun loadLocalAllStaticProduct(): List<LocalProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalProduct(product: LocalProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalProducts(products: List<LocalProduct>)

    @Query("DELETE FROM local_products WHERE product_id = :productId")
    fun deleteLocalProducts(productId: Long)

    @Query("SELECT COUNT(*) FROM local_products")
    fun countLocalTotalProduct(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM local_products")
    fun countLocalTotalProductSync0(): Int

    @Query("SELECT * FROM local_products WHERE barcode = :barcode ")
    fun getProductByBarCode(barcode: String): LiveData<LocalProduct>

    @Query("SELECT * FROM local_products WHERE productName LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<LocalProduct>>

    @Query("SELECT productName FROM local_products WHERE product_id =:product_id ")
    fun getProductNameById(product_id: Long): String

    @Query("SELECT productName FROM local_products WHERE product_id =:product_id ")
    fun isProductExist(product_id: Long): String

    @Query("SELECT * FROM local_products WHERE type =1 ")
    fun loadAllWeightedPrd(): List<LocalProduct>

    @Query("SELECT * FROM local_products WHERE type =1 and subcategoryId=:id ")
    fun loadAllWeightedBySubcategoryId(id:String): List<LocalProduct>

    /*
    * Categories and subcategories
    * */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStoreCategory(storeCategory: StoreCategory)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStoreCategories(storeCategories: List<StoreCategory>)

    @Query("SELECT * FROM store_category")
    fun loadStoreCategory(): LiveData<List<StoreCategory>>

    @Query("SELECT * FROM store_category")
    fun loadStoreCategoryMain(): List<StoreCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubCategories(subCategory: List<SubCategory>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSubCategory(subCategory:SubCategory)

    @Query("SELECT * FROM store_subcategory")
    fun loadSubCategory(): LiveData<List<SubCategory>>


    @Query("SELECT * FROM store_subcategory where categoryId=:categoryId")
    fun loadSubCategoryByCategoryId(categoryId: Long): List<SubCategory>

    @Query("SELECT categoryName FROM store_category where categoryId=:categoryId")
    fun loadSubCategoryNameByCategoryId(categoryId: Long): String

    @Query("SELECT * FROM local_variants where product_id=:id")
    fun loadAllWeightedVaraintByProductId(id:String): LiveData<List<LocalVariant>>
    @Query(value = "SELECT * FROM local_variants WHERE product_id = :productId")
    fun getMasterStaticVariantsOfProducts(productId: Long): List<LocalVariant>

    //Dashboard
    @Query(value = "SELECT SUM(totalPrice) FROM order_item WHERE addedDate = :day")
    fun getAmountOfSalesByDay(day: Date): Double

    @Query(value = "SELECT COUNT(orderitemId) FROM order_item WHERE addedDate = :day")
    fun getNumberOfSalesByDay(day: Date): Double

    @Query(value = "SELECT * FROM order_item WHERE addedDate = :day")
    fun getSalesByDay(day: Date): List<OrderItem>

}
