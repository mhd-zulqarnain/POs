package com.goshoppi.pos.architecture.repository.CreditHistoryRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.CreditHistoryDao
import com.goshoppi.pos.model.local.CreditHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CreditHistoryRepositoryImp @Inject constructor(private var creditHistoryDao: CreditHistoryDao) :
    CreditHistoryRepository {
    override fun loadLocalAllCreditHistoryOfCustomer(customerId: String): LiveData<List<CreditHistory>> {
    return  creditHistoryDao.loadLocalAllCreditHistoryOfCustomer(customerId)
    }

    override suspend fun insertCreditHistory(creditHistory: CreditHistory) {
        withContext(Dispatchers.IO) {
            creditHistoryDao.insertCreditHistory(creditHistory)
        }
    }

    override suspend fun insertCreditHistorys(creditHistorys: List<CreditHistory>) {
        withContext(Dispatchers.IO) {
            creditHistoryDao.insertCreditHistorys(creditHistorys)
        }
    }

    override fun loadLocalAllCreditHistory(): LiveData<List<CreditHistory>> {
        return creditHistoryDao.loadLocalAllCreditHistory()
    }


}