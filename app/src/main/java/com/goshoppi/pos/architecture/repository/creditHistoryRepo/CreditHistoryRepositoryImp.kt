package com.goshoppi.pos.architecture.repository.creditHistoryRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.CreditHistoryDao
import com.goshoppi.pos.model.local.CreditHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject

class CreditHistoryRepositoryImp @Inject constructor(private var creditHistoryDao: CreditHistoryDao) :
    CreditHistoryRepository {
    override suspend fun loadTotalPaidHistoryByDate(upperLimit: Date, lowerLimit: Date): Double {
        return  withContext(Dispatchers.IO) {
            creditHistoryDao.loadTotalPaidHistoryByDate(upperLimit,lowerLimit)
        }

        }

    override suspend fun loadTotalCreditByDate(upperLimit: Date, lowerLimit: Date): Double {
        return  withContext(Dispatchers.IO) {
            creditHistoryDao.loadTotalCreditByDate(upperLimit,lowerLimit)
        }
    }


    override suspend fun totalSalesByDate(upperLimit: Date, lowerLimit: Date): Double {
        return  withContext(Dispatchers.IO) {
            creditHistoryDao.totalSalesByDate(upperLimit,lowerLimit)
        }
    }

    override suspend fun getMonthlyPurchaseByCustomerId(
        customerId: String,
        upperLimit: Date,
        lowerLimit: Date
    ): Double {
        return  withContext(Dispatchers.IO) {
            creditHistoryDao.getMonthlyPurchaseByCustomerId(customerId,upperLimit,lowerLimit)
        }}

    override suspend fun getMonthlyCreditByCustomerId(
        customerId: String,
        upperLimit: Date,
        lowerLimit: Date
    ): Double {
        return withContext(Dispatchers.IO) {
            creditHistoryDao.getMonthlyCreditByCustomerId(customerId,upperLimit,lowerLimit)
        }
    }

    override suspend fun totalSales(): Double {
        return creditHistoryDao.totalSales()
    }

    override suspend fun loadTotalCredit(): Double {
        return creditHistoryDao.loadTotalCredit()
    }

    override suspend fun loadTotalPaidHistory(): Double {
        return  creditHistoryDao.loadTotalPaidHistory()
    }

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