package com.goshoppi.pos.architecture.repository.creditHistoryRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.CreditHistory

interface CreditHistoryRepository {
    fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>>
    suspend fun loadTotalPaidHistory(): Double
    fun loadLocalAllCreditHistoryOfCustomer(customerId:String): LiveData<List<CreditHistory>>

    suspend fun  insertCreditHistory(creditHistory: CreditHistory)
    suspend fun insertCreditHistorys(creditHistorys: List<CreditHistory>)

}