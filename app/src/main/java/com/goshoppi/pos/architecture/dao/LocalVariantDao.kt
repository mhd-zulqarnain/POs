package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.local.LocalVariant

@Dao
interface LocalVariantDao {

    @Query("SELECT * FROM local_variants")
    fun loadAllLocalVariants(): LiveData<List<LocalVariant>>

    @Query("SELECT * FROM local_variants")
    fun loadAllStaticLocalVariants(): List<LocalVariant>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalVariant(variants: LocalVariant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalVariants(variants: List<LocalVariant>)

    @Query("SELECT * FROM local_variants WHERE rangeName LIKE '%' || :param || '%'")
    fun getLocalVariantsSearchResult(param: String): List<LocalVariant>

    @Query(value = "SELECT * FROM local_variants WHERE product_id = :productId")
    fun getLocalVariantsOfProducts(productId: Long): LiveData<List<LocalVariant>>

    @Query("DELETE FROM  local_variants WHERE storeRangeId IN (:varaintIds)")
    fun deleteVaraints(varaintIds: List<Long>)

    @Query("DELETE FROM  local_variants WHERE storeRangeId =:storeRangeId")
    fun deleteVaraint(storeRangeId: Long)

    @Query("SELECT storeRangeId FROM  local_variants WHERE product_id = :productId")
    fun getVaraintIdList(productId: Long): List<Long>

    //    @Query("SELECT local_variants.* , local_products.productName AS productName FROM local_variants LEFT JOIN local_products ON local_variants.product_id=local_products.product_id AND local_variants.barcode = :barcode ")
    @Query("SELECT * FROM local_variants  WHERE barcode = :barcode ")
    fun getVaraintByBarCode(barcode: String): LiveData<LocalVariant>

    @Query("SELECT * FROM local_variants  WHERE storeRangeId = :id ")
    fun getVariantById(id: String): LiveData<LocalVariant>

    @Query("SELECT stockBalance FROM local_variants  WHERE storeRangeId = :varaintId ")
    fun getVaraintStockById(varaintId: String): Int

    @Query("UPDATE local_variants SET stockBalance = :stock WHERE storeRangeId=:varaintId")
    fun updateVariantById(stock: Int, varaintId: String)

    @Query("UPDATE local_variants SET outOfStock = :inStock WHERE storeRangeId=:varaintId")
    fun updateStockStatus(inStock: Boolean, varaintId: String)

    @Query("SELECT productName FROM local_products  WHERE product_id = :prodId ")
    fun getVaraintNameByProdId(prodId: String): String


}
