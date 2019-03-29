package com.goshoppi.pos.architecture.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.goshoppi.pos.model.master.MasterVariant

@Dao
interface MasterVariantDao {

    @Query("SELECT * FROM master_variants")
    fun loadAllMasterVariants(): LiveData<List<MasterVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMasterVariant(variants: MasterVariant)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMasterVariants(variantsList: List<MasterVariant>)

    @Query("SELECT * FROM master_variants WHERE rangeName LIKE '%' || :param || '%'")
    fun getMasterVariantsSearchResult(param: String): List<MasterVariant>

    @Query(value = "SELECT * FROM master_variants WHERE productId = :productId")
    fun getMasterVariantsOfProducts(productId: Int): LiveData<List<MasterVariant>>

    @Query(value = "SELECT * FROM master_variants WHERE productId = :productId")
    fun getMasterStaticVariantsOfProducts(productId: Int): List<MasterVariant>
}