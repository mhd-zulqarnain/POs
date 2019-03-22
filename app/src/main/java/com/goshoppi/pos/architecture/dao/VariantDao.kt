package com.goshoppi.pos.architecture.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterVariant


@Dao
interface VariantDao {

    @Query("SELECT * FROM master_variants")
    fun loadAllVaraints(): LiveData<List<MasterVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVaraint(variants: MasterVariant)

    @Query("SELECT COUNT(*) FROM master_variants")
    fun countTotalVaraint(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM master_variants")
    fun countTotalVaraintSync(): Int

    @Query("SELECT * FROM master_variants WHERE rangeName LIKE '%' || :dealText || '%'")
    fun getSearchResult(dealText: String): List<MasterVariant>

    @Query(value = "SELECT * FROM master_variants WHERE productId = :productId")
    fun getVariantsOfProducts(productId: String): List<MasterVariant>

    /*local database */
    @Query("SELECT * FROM local_variants")
    fun loadLocalAllVaraints(): LiveData<List<LocalVariant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalVaraint(variants: LocalVariant)

    @Query("SELECT COUNT(*) FROM local_variants")
    fun countLocalTotalVaraint(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM local_variants")
    fun countLocalTotalVaraintSync(): Int

    @Query("SELECT * FROM local_variants WHERE rangeName LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): List<LocalVariant>

    @Query(value = "SELECT * FROM local_variants WHERE productId = :productId")
    fun getLocalVariantsOfProducts(productId: String): List<LocalVariant>

}
