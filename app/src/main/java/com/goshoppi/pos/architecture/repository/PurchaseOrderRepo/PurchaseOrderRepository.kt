package com.goshoppi.pos.architecture.repository.PurchaseOrderRepo

import com.goshoppi.pos.model.local.PoHistory
import com.goshoppi.pos.model.local.PurchaseOrder
import com.goshoppi.pos.model.local.PurchaseOrderDetails

interface PurchaseOrderRepository {
    suspend fun insertPurchaseOrderDetails(purchaseOrderDetails: List<PurchaseOrderDetails>)
    suspend fun insertPurchaseOrder(purchaseOrder: PurchaseOrder):Long
    suspend fun insertPoHistory(poHistory: PoHistory)
    suspend  fun getDistributorsStaticCredit(customerId: String): Double
    suspend fun updateCredit(distId: String,credit:Double,date:String)

}