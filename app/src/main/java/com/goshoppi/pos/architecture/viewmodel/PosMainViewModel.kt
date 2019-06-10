package com.goshoppi.pos.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.creditHistoryRepo.CreditHistoryRepository
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.orderItemRepo.OrderItemRepository
import com.goshoppi.pos.architecture.repository.orderRepo.OrderRepository
import com.goshoppi.pos.model.Flag
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.CreditHistory
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants.ANONYMOUS
import com.goshoppi.pos.utils.Constants.CREDIT
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.System.currentTimeMillis
import javax.inject.Inject


class PosMainViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository,
    var orderRepository: OrderRepository,
    var orderItemRepository: OrderItemRepository,
    var localCustomerRepository: CustomerRepository,
    var localVariantRepository: LocalVariantRepository,
    var creditHistoryRepository: CreditHistoryRepository,
    var customerRepository: CustomerRepository
) : ViewModel() {

    var flag: MutableLiveData<Flag> = MutableLiveData()
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var productBarCode: MutableLiveData<String> = MutableLiveData()
    var weightedVariantid: MutableLiveData<String> = MutableLiveData()
    var searchNameParam: MutableLiveData<String> = MutableLiveData()
    var holdedCount: MutableLiveData<String> = MutableLiveData()
    var customer: LocalCustomer = getAnonymousCustomer()
    var orderItemList: ArrayList<OrderItem> = ArrayList()
    var subtotal = 0.00
    var orderId: Long = currentTimeMillis()

    var productObservable: LiveData<LocalVariant> = Transformations.switchMap(productBarCode) { barcode ->
        localVariantRepository.getVariantByBarCode(barcode)
    }

    var weightedProductObservable: LiveData<LocalVariant> = Transformations.switchMap(weightedVariantid) { id ->
        localVariantRepository.getVariantById(id)

    }

    var cutomerListObservable: LiveData<List<LocalCustomer>> = Transformations.switchMap(searchNameParam) { name ->
        localCustomerRepository.searchLocalCustomers(name)

    }

    fun search(barcode: String) {
        productBarCode.value = barcode
    }

    fun searchWeightedVariantByid(id: Long) {
        weightedVariantid.value = id.toString()
    }

    fun searchCustomer(name: String) {
        searchNameParam.value = name

    }

    fun setFlag(obj: Flag) {
        flag.value = obj

    }

    fun placeOrder(paymentType: String, discountAmount: Double) {
//        productBarCode.value = barcode
        if (paymentType == CREDIT && customer.name == ANONYMOUS) {
            setFlag(Flag(false, "Please add Customer details for Credit"))
        } else if (subtotal < 1 || orderItemList.size == 0) {
            setFlag(Flag(false, "Please Add products to place order"))

        } else {
            val order = Order()
            order.orderId = orderId
            order.storeChainId = 0
            order.orderNum = 0
            order.discount = discountAmount.toString()
            order.paymentStatus = paymentType
            order.orderDate = Utils.getTodaysDate()
            order.customerId = customer.phone
            order.customerName = customer.name
            order.customerMobile = customer.phone
            order.customerAddress = customer.address
            order.orderAmount = subtotal.toString()
            order.addedDate = Utils.getTodaysDate()
            if (paymentType == CREDIT) {
                updateCredit(order)
            }
            uiScope.launch {
                orderItemRepository.insertOrderItems(orderItemList)
                /*After placing order setting argument empty to prevent trigger on updating local variant*/
                searchNameParam.value = ANONYMOUS
                productBarCode.value = ""
                /*updating stock of variant*/
                orderItemList.forEach { variant ->
                    val stock = localVariantRepository.getVaraintStockById(varaintId = variant.variantId.toString())
                    try {
                        val newStock = stock - variant.productQty!!.toInt()
                        if (newStock <= 0) {
                            localVariantRepository.updateStockStatus(false, variant.variantId.toString())
                        }
                        localVariantRepository.updateVarianStocktById(
                            newStock,
                            varaintId = variant.variantId.toString()
                        )
                    } catch (e: Exception) {
                        Timber.e(e)
                        Timber.e("Exception")
                        return@launch
                    }
                }

                orderRepository.insertOrder(order)

                setFlag(Flag(true, "Order placed successfully"))

            }
        }
        if (customer.name == ANONYMOUS && paymentType != CREDIT) {
            addCustomer(customer)
        }
    }

    private fun updateCredit(order: Order) {

        val transaction = CreditHistory()
        transaction.customerId = order.customerId
        transaction.orderId = order.orderId
        transaction.paidAmount = 0.0
        transaction.transcationDate = Utils.getTodaysDate()
        transaction.creditAmount = subtotal

        uiScope.launch {
            var credit = 0.00
            val it = customerRepository.getCustomerStaticCredit(customer.phone.toString())

            if (it != 0.00) {
                credit = subtotal + it
            } else
                credit = subtotal

            transaction.totalCreditAmount = credit
            customerRepository.updateCredit(order.customerId.toString(), credit, System.currentTimeMillis().toString())
            creditHistoryRepository.insertCreditHistory(transaction)

        }

    }

    fun addCustomer(customer: LocalCustomer) {
        uiScope.launch {
            localCustomerRepository.insertLocalCustomer(customer)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

    fun getAnonymousCustomer(): LocalCustomer {
        val temp = LocalCustomer()
        temp.phone = 100000000000
        temp.alternativePhone = "100000000000"
        temp.gstin = "sANO"
        temp.name = ANONYMOUS
        temp.address = ANONYMOUS
        temp.isSynced = false
        temp.updatedAt = currentTimeMillis().toString()
        return temp
    }
}
