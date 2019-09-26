package com.goshoppi.pos.architecture.repository.PurchaseOrderRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.local.PoHistory
import com.goshoppi.pos.model.local.PurchaseOrder
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import java.util.*

interface PurchaseOrderRepository {
    suspend fun insertPurchaseOrderDetails(purchaseOrderDetails: List<PurchaseOrderDetails>)
    suspend fun insertPurchaseOrder(purchaseOrder: PurchaseOrder):Long
    suspend fun insertPoHistory(poHistory: PoHistory)
    suspend  fun getDistributorsStaticCredit(customerId: String): Double
    suspend fun updateCredit(distId: String,credit:Double,date:Date)
    fun loadLocalAllCreditHistory(distId: String): LiveData<List<PoHistory>>
    fun loadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber: Long): LiveData<List<PurchaseOrderDetails>>
    fun loadPurcahseOrderDetailByDistributorId(id: Long): LiveData<List<PurchaseOrderDetails>>
    suspend  fun aysnloadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber: Long): List<PurchaseOrderDetails>

}