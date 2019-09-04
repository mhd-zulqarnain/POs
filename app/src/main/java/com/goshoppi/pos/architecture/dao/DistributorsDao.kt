package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PurchaseOrder


@Dao
interface DistributorsDao {

    @Query("SELECT * FROM distributors")
    fun loadLocalAllDistributor(): LiveData<List<Distributor>>

    @Query("SELECT * FROM distributors")
    fun loadLocalAllStaticDistributor(): List<Distributor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistributor(Distributor: Distributor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistributors(Distributor: List<Distributor>)

    @Query("DELETE FROM  distributors WHERE  phone= :phoneId")
    fun deleteDistributors(phoneId: Long)

    @Query("SELECT * FROM  distributors WHERE name LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<Distributor>>

    @Query("SELECT * FROM  distributors WHERE name LIKE '%' || :dealText || '%'")
    fun getLocalSearchStaticResult(dealText: String): List<Distributor>


    @Query("SELECT totalCredit FROM distributors WHERE phone=:distributorId ")
    fun getDistributorCredit(distributorId: String): LiveData<Double>

    @Query("SELECT totalCredit FROM distributors WHERE phone=:distributorId ")
    fun getDistributorStaticCredit(distributorId: String): Double

    @Query("Update distributors set totalCredit=:credit ,updatedAt =:date where phone=:distributorId")
    fun updateCredit(distributorId: String,credit:Double,date:String)

    @Query("SELECT SUM(totalCredit) FROM distributors ")
    fun getTotalDebit(): LiveData<Double>

    @Query("SELECT COUNT(*) FROM purchase_order WHERE distributorId=:distributorId ")
    fun getTotalOrder(distributorId: String): LiveData<Int>

    @Query("SELECT SUM(totalAmount) FROM purchase_order WHERE distributorId=:distributorId ")
    fun getTotalTransaction(distributorId: String): LiveData<Int>

    @Query("SELECT * FROM purchase_order WHERE distributorId=:distributorId ")
    fun getListOfOrders(distributorId: String): LiveData<List<PurchaseOrder>>

    @Query("SELECT COUNT(phone )FROM distributors ")
    fun getTotalDistributor(): Long
}
