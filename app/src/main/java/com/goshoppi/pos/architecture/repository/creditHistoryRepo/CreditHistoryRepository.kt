package com.goshoppi.pos.architecture.repository.creditHistoryRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.CreditHistory

interface CreditHistoryRepository {
    fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>>
    suspend fun loadTotalPaidHistory(): Double
    suspend fun loadTotalCredit(): Double
    fun loadLocalAllCreditHistoryOfCustomer(customerId:String): LiveData<List<CreditHistory>>

    suspend fun  insertCreditHistory(creditHistory: CreditHistory)
    suspend fun  totalSales():Double
    suspend fun insertCreditHistorys(creditHistorys: List<CreditHistory>)

}