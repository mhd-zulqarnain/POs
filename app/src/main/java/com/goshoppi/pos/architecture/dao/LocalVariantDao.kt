package com.goshoppi.pos.architecture.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.goshoppi.pos.model.local.LocalVariant

@Dao
interface LocalVariantDao {

    @Query("SELECT * FROM local_variants")
    fun loadAllLocalVariants(): LiveData<List<LocalVariant>>

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
    fun getVaraintIdList(productId: Int):LiveData<List<Int>>


}
