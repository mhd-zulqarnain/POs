package com.goshoppi.pos.architecture

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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertProduct(product: Product)

    @Query("SELECT COUNT(*) FROM products")
    fun countTotalProduct(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM products")
    fun countTotalProductSync0(): Int
}
