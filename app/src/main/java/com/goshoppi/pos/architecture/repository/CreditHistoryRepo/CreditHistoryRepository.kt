package com.goshoppi.pos.architecture.repository.CreditHistoryRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.CreditHistory

interface CreditHistoryRepository {
    fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>>
    fun loadLocalAllCreditHistoryOfCustomer(customerId:String): LiveData<List<CreditHistory>>

    suspend fun  insertCreditHistory(creditHistory: CreditHistory)
    suspend fun insertCreditHistorys(creditHistorys: List<CreditHistory>)

}