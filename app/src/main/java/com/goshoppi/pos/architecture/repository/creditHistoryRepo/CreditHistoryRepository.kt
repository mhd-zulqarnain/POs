package com.goshoppi.pos.architecture.repository.creditHistoryRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.CreditHistory
import java.util.*

interface CreditHistoryRepository {
    fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>>
    suspend fun loadTotalPaidHistory(): Double
    suspend fun loadTotalCredit(): Double
    fun loadLocalAllCreditHistoryOfCustomer(customerId: String): LiveData<List<CreditHistory>>

    suspend fun insertCreditHistory(creditHistory: CreditHistory)
    suspend fun totalSales(): Double
    suspend fun insertCreditHistorys(creditHistorys: List<CreditHistory>)

    suspend fun getMonthlyPurchaseByCustomerId(
        customerId: String,
        upperLimit: Date,
        lowerLimit: Date
    ): Double

    suspend fun loadTotalPaidHistoryByDate(
        upperLimit: Date,
        lowerLimit: Date
    ): Double

    suspend fun getMonthlyCreditByCustomerId(customerId: String,
                                             upperLimit:Date ,
                                             lowerLimit:Date ):Double
    suspend fun loadTotalCreditByDate( upperLimit:Date ,
                                             lowerLimit:Date ):Double
    suspend fun totalSalesByDate( upperLimit:Date ,
                                             lowerLimit:Date ):Double

}