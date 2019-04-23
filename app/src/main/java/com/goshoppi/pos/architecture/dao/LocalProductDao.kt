package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.goshoppi.pos.model.local.LocalProduct


@Dao
interface LocalProductDao {

    @Query("SELECT * FROM local_products")
    fun loadLocalAllProduct(): LiveData<List<LocalProduct>>

 @Query("SELECT * FROM local_products")
    fun loadAllStaticLocalProduct(): List<LocalProduct>

    @Query("SELECT * FROM local_products")
    fun loadLocalAllStaticProduct(): List<LocalProduct>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalProduct(product: LocalProduct)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocalProducts(products:  List<LocalProduct>)

    @Query("DELETE FROM local_products WHERE product_id = :productId")
    fun deleteLocalProducts(productId: Int)

    @Query("SELECT COUNT(*) FROM local_products")
    fun countLocalTotalProduct(): LiveData<Int>

    @Query("SELECT COUNT(*) FROM local_products")
    fun countLocalTotalProductSync0(): Int

    @Query("SELECT * FROM local_products WHERE barcode = :barcode ")
    fun getProductByBarCode(barcode:String): LiveData<LocalProduct>

    @Query("SELECT * FROM local_products WHERE productName LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<LocalProduct>>
}
