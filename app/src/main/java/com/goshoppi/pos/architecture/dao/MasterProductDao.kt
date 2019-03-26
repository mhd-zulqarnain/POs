package com.goshoppi.pos.architecture.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.goshoppi.pos.model.master.MasterProduct


@Dao
interface MasterProductDao {
    @Query("SELECT * FROM master_products")
    fun loadAllProduct(): LiveData<List<MasterProduct>>

    @Query("SELECT * FROM master_products")
    fun loadAllStaticProduct(): List<MasterProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: MasterProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProducts(products: List<MasterProduct>)

    @Query("SELECT COUNT(*) FROM master_products")
    fun countTotalProduct(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM master_products")
    fun countTotalProductSync0(): Int

    @Query("SELECT * FROM master_products WHERE productName LIKE '%' || :dealText || '%'")
    fun getSearchResult(dealText: String): LiveData<List<MasterProduct>>

}
