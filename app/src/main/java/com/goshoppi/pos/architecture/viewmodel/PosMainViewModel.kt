package com.goshoppi.pos.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.orderItemRepo.OrderItemRepository
import com.goshoppi.pos.architecture.repository.orderRepo.OrderRepository
import com.goshoppi.pos.model.Flag
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Constants.ANONYMOUS
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.*
import java.lang.System.currentTimeMillis
import javax.inject.Inject

class PosMainViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository,
    var orderRepository: OrderRepository,
    var orderItemRepository: OrderItemRepository,
    var localCustomerRepository: CustomerRepository,
    var localVariantRepository: LocalVariantRepository
) : ViewModel()
{

    var flag: MutableLiveData<Flag> = MutableLiveData()
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var productBarCode: MutableLiveData<String> = MutableLiveData()
    var searchNameParam: MutableLiveData<String> = MutableLiveData()
    var customer: LocalCustomer = getAnonymousCustomer()
    var orderItemList: ArrayList<OrderItem> = ArrayList()
    var totalAmount = 0.00
    var orderId:Long = currentTimeMillis()

    var productObservable: LiveData<LocalVariant> = Transformations.switchMap(productBarCode) { barcode ->
        localVariantRepository.getVariantByBarCode(barcode)

    }

    var cutomerListObservable: LiveData<List<LocalCustomer>> = Transformations.switchMap(searchNameParam) { name ->
        localCustomerRepository.searchLocalCustomers(name)
    }
    
    fun search(barcode: String) {
        productBarCode.value = barcode
    }

    fun searchCustomer(name: String) {
        searchNameParam.value = name
    }

    fun setFlag(obj: Flag) {
        flag.value = obj
    }

    fun placeOrder(paymentType: String) {
//        productBarCode.value = barcode
        if (paymentType == Constants.CREDIT && customer.name == Constants.ANONYMOUS) {
            setFlag(Flag(false, "Please add Customer details for Credit"))
        } else if (totalAmount < 1 || orderItemList.size == 0) {
            setFlag(Flag(false, "Please Add products to place order"))

        } else {
            uiScope.launch {

                val order = Order()
                order.orderId = orderId
                order.storeChainId = 0
                order.orderNum = 0
                order.paymentStatus = paymentType
                order.orderDate = Utils.getTodaysDate()
                order.customerId = customer.phone
                order.customerName = customer.name
                order.customerMobile = customer.phone
                order.customerAddress = customer.address
                order.orderAmount = totalAmount.toString()
                order.addedDate = Utils.getTodaysDate()
                orderItemRepository.insertOrderItems(orderItemList)
                /*updating stock of variant*/
                orderItemList.forEach { variant ->
                  val stock=  localVariantRepository.getVaraintStockById(varaintId = variant.variantId.toString())
                    val newStock = stock.toInt()-variant.productQty!!.toInt()
                    if(newStock<=0){
                        localVariantRepository.updateStockStatus(false,variant.variantId.toString())
                    }
                    localVariantRepository.updateVarianStocktById(newStock,varaintId = variant.variantId.toString())
                }

                orderRepository.insertOrder(order)

                setFlag(Flag(true, "Order placed successfully"))
            }
        }
        if (customer.name == Constants.ANONYMOUS && paymentType != Constants.CREDIT) {
            addCustomer(customer)
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
