package com.goshoppi.pos.architecture.repository.distributorsRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.DistributorsDao
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PurchaseOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DistributorsRepositoryImp @Inject
constructor(private val distributorsDao: DistributorsDao) : DistributorsRepository {
    override fun getTotalDistributor(): Long {
        return distributorsDao.getTotalDistributor()
    }

    override fun getListOfOrders(distributorId: String): LiveData<List<PurchaseOrder>> {
        return  distributorsDao.getListOfOrders(distributorId)
    }

    override fun getDisTotalTransaction(distributorId: String): LiveData<Int> {
        return distributorsDao.getTotalTransaction(distributorId)
    }

    override fun getTotalOrder(distributorId: String): LiveData<Int> {
        return  distributorsDao.getTotalOrder(distributorId)
    }

    override suspend fun getDistributorStaticCredit(distributorId: String): Double {
        return withContext(Dispatchers.IO) { distributorsDao.getDistributorStaticCredit(distributorId) }
    }

    suspend override fun loadAllStaticDistributor(): List<Distributor> {
        return withContext(Dispatchers.IO) { distributorsDao.loadLocalAllStaticDistributor() }
    }

    override suspend fun getTotalDebit(): LiveData<Double> {
        return withContext(Dispatchers.IO) { distributorsDao.getTotalDebit() }
    }


    override suspend fun updateCredit(distributorId: String, credit: Double, date: String) {
        withContext(Dispatchers.IO) {
            distributorsDao.updateCredit(distributorId, credit, date)
        }
    }


    override suspend fun getDistributorCredit(distributorId: String): LiveData<Double> {
        return withContext(Dispatchers.IO) { distributorsDao.getDistributorCredit(distributorId) }
    }


    override suspend fun searchLocalStaticDistributors(param: String): List<Distributor> {
        return withContext(Dispatchers.IO) {
            distributorsDao.getLocalSearchStaticResult(param)
        }
    }

    override fun loadAllDistributor(): LiveData<List<Distributor>> {
        return distributorsDao.loadLocalAllDistributor()
    }

    override suspend fun insertDistributor(Distributor: Distributor) {
        withContext(Dispatchers.IO) {
            distributorsDao.insertDistributor(Distributor)
        }
    }

    override fun insertDistributors(DistributorList: List<Distributor>) {
        distributorsDao.insertDistributors(DistributorList)
    }

    override fun searchDistributors(param: String): LiveData<List<Distributor>> {
        return distributorsDao.getLocalSearchResult(param)
    }

    override suspend fun deleteDistributors(phoneId: Long) {
        withContext(Dispatchers.IO) { distributorsDao.deleteDistributors(phoneId) }
    }
}