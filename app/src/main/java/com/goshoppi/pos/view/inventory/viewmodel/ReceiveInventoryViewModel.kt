package com.goshoppi.pos.view.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.PurchaseOrderRepo.PurchaseOrderRepository
import com.goshoppi.pos.architecture.repository.distributorsRepo.DistributorsRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.model.Flag
import com.goshoppi.pos.model.local.*
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class ReceiveInventoryViewModel @Inject constructor(
    var localVariantRepository: LocalVariantRepository,
    var distributorsRepository: DistributorsRepository,
    var purchaseOrderRepository: PurchaseOrderRepository

) : ViewModel() {

    var flag: MutableLiveData<Flag> = MutableLiveData()
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    var discount = 0.00
    var productBarCode: MutableLiveData<String> = MutableLiveData()
    var searchNameParam: MutableLiveData<String> = MutableLiveData()
    var distributor: Distributor? = null
    var poDetailList: ArrayList<PurchaseOrderDetails> = ArrayList()
    var subtotal = 0.00

    var productObservable: LiveData<LocalVariant> = Transformations.switchMap(productBarCode) { barcode ->
        localVariantRepository.getVariantByBarCode(barcode)

    }

    var cutomerListObservable: LiveData<List<Distributor>> = Transformations.switchMap(searchNameParam) { name ->
        distributorsRepository.searchDistributors(name)

    }

    fun addDistributor(distributor: Distributor) {
        uiScope.launch {
            distributorsRepository.insertDistributor(distributor)
        }
    }

    fun search(barcode: String) {
        productBarCode.value = barcode

    }

    fun searchdistributor(name: String) {
        searchNameParam.value = name

    }

    fun placeOrder(invoiceNo: String, pOdate: String, cash: String, credit: String) {
//        productBarCode.value = barcode
        if (distributor == null) {
            setFlag(Flag(false, "Please add distributor details "))
            } else if (subtotal < 1 || poDetailList.size == 0) {
            setFlag(Flag(false, "Please Add products to place order"))

        } else {
            val poOrder = PurchaseOrder()
            poOrder.poInvoiceNumber = invoiceNo.toLong()
            poOrder.paid = if(cash.isEmpty()) 0.00 else cash.toDouble()
            poOrder.credit = if(credit.isEmpty()) 0.00 else credit.toDouble()
            poOrder.paymentType = "cash"
            poOrder.poDate = pOdate
            poOrder.distributorId = distributor?.phone.toString()
            poOrder.distributorName = distributor?.name
            poOrder.distributorMobile = distributor?.phone.toString()
            poOrder.distributorAddress = distributor?.address
            poOrder.totalAmount = subtotal.toLong()

            uiScope.launch {
                val id: Long = purchaseOrderRepository.insertPurchaseOrder(poOrder)
                searchNameParam.value ="-1"
                productBarCode.value = "-1"
                /*updating stock of variant*/
                poDetailList.forEach { pod ->
                    pod.poId = id
                    pod.distributorId = distributor?.phone
                    val stock = localVariantRepository.getVaraintStockById(varaintId = pod.variantId.toString())
                    try {
                        if (stock <= 0) {

                            localVariantRepository.updateStockStatus(false, pod.variantId.toString())
                        } else {
                            val newStock = stock + pod.productQty!!.toInt()
                            localVariantRepository.updateVarianStocktById(
                                newStock,
                                varaintId = pod.variantId.toString()
                            )
                        }

                    } catch (e: Exception) {
                        return@launch
                    }
                }
                updateCredit(poOrder, id,poOrder.paid,poOrder.credit)
                purchaseOrderRepository.insertPurchaseOrderDetails(poDetailList)
                setFlag(Flag(true, "Order placed successfully"))

            }
        }

    }


    private fun updateCredit(
        po: PurchaseOrder,
        poId: Long,
        paid: Double,
        crd: Double
    ) {

        val poHistory = PoHistory()
        poHistory.distributorId = po.distributorId!!.toLong()
        poHistory.poId = poId
        poHistory.paidAmount = paid
        poHistory.transcationDate = Utils.getTodaysDate()
        poHistory.creditAmount = po.credit

        uiScope.launch {
            var credit =crd
            val it = purchaseOrderRepository.getDistributorsStaticCredit(distributor!!.phone.toString())

            if (it != 0.00) {
                credit = po.credit + it
            } else
                credit = po.credit

//            poHistory.totalCreditAmount = credit
            purchaseOrderRepository.updateCredit(
                poHistory.distributorId.toString(),
                credit, System.currentTimeMillis().toString()
            )
            purchaseOrderRepository.insertPoHistory(poHistory)

        }

    }


    fun setFlag(obj: Flag) {
        flag.value = obj

    }

    /** LocalInventoryActivity Local Product Variants Work END*/
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}