package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.StoreCategory
import com.goshoppi.pos.model.SubCategory
import com.goshoppi.pos.model.master.MasterProduct


@Dao
interface MasterProductDao {
    @Query("SELECT * FROM master_products")
    fun loadAllProduct(): LiveData<List<MasterProduct>>

    @Query("SELECT * FROM master_products")
    fun loadAllStaticProduct(): List<MasterProduct>

    @Query("SELECT * FROM master_products WHERE productName LIKE '%' || :dealText || '%'")
    fun loadAllPaginatedMasterSearchProduct(dealText: String): DataSource.Factory<Int,MasterProduct>

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
