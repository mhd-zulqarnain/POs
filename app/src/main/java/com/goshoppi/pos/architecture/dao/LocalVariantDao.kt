package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.local.LocalVaraintsWithProductName
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
    fun getLocalVariantsOfProducts(productId: Int): LiveData<List<LocalVariant>>

    @Query("DELETE FROM  local_variants WHERE storeRangeId IN (:varaintIds)")
    fun deleteVaraints(varaintIds:List<Int> )

    @Query("DELETE FROM  local_variants WHERE storeRangeId =:storeRangeId")
    fun deleteVaraint(storeRangeId:Int )

    @Query("SELECT storeRangeId FROM  local_variants WHERE product_id = :productId")
    fun getVaraintIdList(productId: Int):List<Int>

    @Query("SELECT local_variants.* , local_products.productName AS productName FROM local_variants LEFT JOIN local_products ON local_variants.product_id=local_products.product_id AND local_variants.barcode = :barcode ")
    fun getVaraintByBarCode(barcode:String): LiveData<LocalVaraintsWithProductName>

}
