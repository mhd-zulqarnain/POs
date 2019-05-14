package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.Distributor


@Dao
interface PurchaseOrderDao {

    @Query("SELECT * FROM distributors")
    fun loadLocalAllDistributor(): LiveData<List<Distributor>>

    @Query("SELECT * FROM distributors")
    fun loadLocalAllStaticDistributor(): List<Distributor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistributor(Distributor: Distributor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistributors(Distributor: List<Distributor>)

}
