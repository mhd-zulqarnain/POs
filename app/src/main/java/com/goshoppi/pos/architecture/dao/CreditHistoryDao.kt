package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.local.CreditHistory
import java.util.*

@Dao
interface CreditHistoryDao {

    @Query("SELECT * FROM credit_history")
    fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>>

    @Query("SELECT * FROM credit_history where customerId=:customerId")
    fun loadLocalAllCreditHistoryOfCustomer(customerId: String): LiveData<List<CreditHistory>>

    @Query("SELECT * FROM credit_history")
    fun loadLocalAllStaticCreditHistory(): List<CreditHistory>

    @Query("SELECT SUM(paidAmount) FROM credit_history")
    fun loadTotalPaidHistory(): Double

    @Query("SELECT SUM(totalCredit) FROM local_customers")
    fun loadTotalCredit(): Double

    @Query("SELECT SUM(paidAmount + totalCreditAmount) FROM credit_history")
    fun totalSales(): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCreditHistory(CreditHistory: CreditHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCreditHistorys(credit_history: List<CreditHistory>)

    /*quries by date
    * */

    @Query("SELECT SUM(paidAmount) FROM credit_history WHERE customerId=:customerId AND  (transcationDate BETWEEN :upperLimit AND :lowerLimit) ")
    fun getMonthlyPurchaseByCustomerId(
        customerId: String,
        upperLimit: Date,
        lowerLimit: Date
    ): Double

    @Query("SELECT SUM(creditAmount) FROM credit_history WHERE customerId=:customerId AND  (transcationDate BETWEEN :upperLimit AND :lowerLimit) ")
    fun getMonthlyCreditByCustomerId(
        customerId: String,
        upperLimit: Date,
        lowerLimit: Date
    ): Double

    @Query("SELECT SUM(paidAmount) FROM credit_history WHERE transcationDate BETWEEN :upperLimit AND :lowerLimit ")
    fun loadTotalPaidHistoryByDate(
        upperLimit: Date,
        lowerLimit: Date
    ): Double

    @Query("SELECT SUM(totalCredit) FROM local_customers WHERE updatedAt BETWEEN :upperLimit AND :lowerLimit ")
    fun loadTotalCreditByDate(
        upperLimit: Date,
        lowerLimit: Date
    ): Double

    @Query("SELECT SUM(paidAmount + totalCreditAmount) FROM credit_history WHERE transcationDate BETWEEN :upperLimit AND :lowerLimit ")
    fun totalSalesByDate(
        upperLimit: Date,
        lowerLimit: Date
    ): Double

}

