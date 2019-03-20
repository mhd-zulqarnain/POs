package com.goshoppi.pos.architecture

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.model.Variant


@Dao
interface VariantDao {

    @Query("SELECT * FROM variants")
    fun loadAllVaraints(): LiveData<List<Variant>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVaraint(variants: Variant)

    @Query("SELECT COUNT(*) FROM variants")
    fun countTotalVaraint(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM variants")
    fun countTotalVaraintSync(): Int

    @Query("SELECT * FROM variants WHERE rangeName LIKE '%' || :dealText || '%'")
    fun getSearchResult(dealText: String): List<Variant>
}
