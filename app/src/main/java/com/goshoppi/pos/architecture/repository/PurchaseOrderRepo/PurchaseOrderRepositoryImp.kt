package com.goshoppi.pos.architecture.repository.PurchaseOrderRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.PurchaseOrderDao
import com.goshoppi.pos.model.local.PoHistory
import com.goshoppi.pos.model.local.PurchaseOrder
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PurchaseOrderRepositoryImp @Inject constructor(var pODao: PurchaseOrderDao):PurchaseOrderRepository{
    override fun loadPurcahseOrderDetailByDistributorId(id: Long): LiveData<List<PurchaseOrderDetails>> {
        return pODao.loadPurcahseOrderDetailByDistributorId(id) }

    override suspend fun aysnloadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber: Long): List<PurchaseOrderDetails> {
        return pODao.aysnloadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber)
    }

    override fun loadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber: Long): LiveData<List<PurchaseOrderDetails>> {
        return pODao.loadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber)
    }

    override fun loadLocalAllCreditHistory(distId: String): LiveData<List<PoHistory>> {
        return pODao.loadLocalAllCreditHistory(distId)
    }

    override suspend fun updateCredit(distId: String, credit: Double, date: String) {
        return withContext(Dispatchers.IO) {pODao.updateCredit(distId,credit,date)
        }
    }

    override suspend fun getDistributorsStaticCredit(customerId: String): Double {
     return withContext(Dispatchers.IO) {pODao.getDistributorsStaticCredit(customerId)}
    }

    override suspend fun insertPoHistory(poHistory: PoHistory) {
        withContext(Dispatchers.IO) { pODao.insertPoHistoryHistory(poHistory)}
    }

    override suspend fun insertPurchaseOrderDetails(purchaseOrderDetails: List<PurchaseOrderDetails>) {
        withContext(Dispatchers.IO) { pODao.insertPurchaseOrderDetails(purchaseOrderDetails)}
    }

    override suspend fun insertPurchaseOrder(purchaseOrder: PurchaseOrder) :Long{
       return withContext(Dispatchers.IO) {pODao.insertPurchaseOrder(purchaseOrder) }
    }

}