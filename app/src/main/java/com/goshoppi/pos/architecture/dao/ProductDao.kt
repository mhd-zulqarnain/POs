package com.goshoppi.pos.architecture.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.goshoppi.pos.model.Product


@Dao
interface ProductDao {

    @Query("SELECT * FROM products")
    fun loadAllProduct(): LiveData<List<Product>>

    @Query("SELECT * FROM products")
    fun loadAllStaticProduct(): List<Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("SELECT COUNT(*) FROM products")
    fun countTotalProduct(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM products")
    fun countTotalProductSync0(): Int

    @Query("SELECT * FROM products WHERE productName LIKE '%' || :dealText || '%'")
    fun getSearchResult(dealText: String): List<Product>
}
