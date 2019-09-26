package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.local.Distributor
import com.goshoppi.pos.model.local.PoHistory
import com.goshoppi.pos.model.local.PurchaseOrder
import com.goshoppi.pos.model.local.PurchaseOrderDetails
import java.util.*


@Dao
interface PurchaseOrderDao {

    @Query("SELECT * FROM distributors")
    fun loadLocalAllDistributor(): LiveData<List<Distributor>>

    @Query("SELECT * FROM distributors")
    fun loadLocalAllStaticDistributor(): List<Distributor>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistributor(Distributor: Distributor)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDistributors(Distributor: List<Distributor>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPurchaseOrderDetails(purchaseOrderDetails: List<PurchaseOrderDetails>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPurchaseOrder(purchaseOrder: PurchaseOrder):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertPoHistoryHistory(poHistory: PoHistory)

    @Query("SELECT totalCredit FROM distributors WHERE phone=:customerId ")
    fun getDistributorsStaticCredit(customerId: String): Double

    @Query("Update distributors set totalCredit=:credit ,updatedAt =:date where phone=:distId")
    fun updateCredit(distId: String,credit:Double,date: Date)

    @Query("SELECT * FROM po_history where distributorId=:distId")
    fun loadLocalAllCreditHistory(distId: String): LiveData<List<PoHistory>>

    @Query("SELECT * FROM purchase_order_details where poInvoiceNumber=:poInvoiceNumber")
    fun loadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber: Long): LiveData<List<PurchaseOrderDetails>>

    @Query("SELECT * FROM purchase_order_details where distributorId=:id")
    fun loadPurcahseOrderDetailByDistributorId(id: Long): LiveData<List<PurchaseOrderDetails>>

    @Query("SELECT * FROM purchase_order_details where poInvoiceNumber=:poInvoiceNumber")
    fun aysnloadPurcahseOrderDetailByInvoiceNumber(poInvoiceNumber: Long): List<PurchaseOrderDetails>

}
