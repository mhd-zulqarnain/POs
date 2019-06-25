package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    fun getMasterVariantsOfProducts(productId: Long): List<MasterVariant>

    @Query(value = "SELECT * FROM master_variants WHERE productId = :productId")
    fun getMasterStaticVariantsOfProducts(productId: Long): List<MasterVariant>
}