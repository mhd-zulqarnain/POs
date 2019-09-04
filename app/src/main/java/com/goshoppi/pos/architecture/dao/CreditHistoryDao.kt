package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.local.CreditHistory

@Dao
interface CreditHistoryDao {

    @Query("SELECT * FROM credit_history")
    fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>>

    @Query("SELECT * FROM credit_history where customerId=:customerId")
    fun loadLocalAllCreditHistoryOfCustomer(customerId:String): LiveData<List<CreditHistory>>

    @Query("SELECT * FROM credit_history")
    fun loadLocalAllStaticCreditHistory(): List<CreditHistory>

    @Query("SELECT SUM(paidAmount) FROM credit_history")
    fun loadTotalPaidHistory(): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCreditHistory(CreditHistory: CreditHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCreditHistorys(credit_history: List<CreditHistory>)

}
