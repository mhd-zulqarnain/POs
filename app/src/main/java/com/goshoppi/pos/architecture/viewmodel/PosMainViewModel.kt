package com.goshoppi.pos.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.customerRepo.CustomerRepository
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.orderItemRepo.OrderItemRepository
import com.goshoppi.pos.architecture.repository.orderRepo.OrderRepository
import com.goshoppi.pos.model.Flag
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.ArrayList

class PosMainViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository,
    var orderRepository: OrderRepository,
    var orderItemRepository: OrderItemRepository,
    var localCustomerRepository: CustomerRepository
) : ViewModel() {

    var flag: MutableLiveData<Flag> = MutableLiveData()
    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var productBarCode: MutableLiveData<String> = MutableLiveData()
    var searchNameParam: MutableLiveData<String> = MutableLiveData()
    var customer: LocalCustomer? = null
    var orderItemList: ArrayList<OrderItem> = ArrayList()
    var totalAmount = 0.00
    var orderId = System.currentTimeMillis()

    var productObservable: LiveData<LocalProduct> = Transformations.switchMap(productBarCode) { barcode ->
        Timber.e("productObservable Transformations Runs")
        localProductRepository.getProductByBarCode(barcode)

    }

    var cutomerListObservable: LiveData<List<LocalCustomer>> = Transformations.switchMap(searchNameParam) { name ->
        Timber.e("cutomerListObservable Transformations Runs")
        localCustomerRepository.searchLocalCustomers(name)
    }


    fun search(barcode: String) {
        productBarCode.value = barcode
    }

    fun searchCustomer(name: String) {
        searchNameParam.value = name
    }

    fun setFlag(obj:Flag){
        flag.value = obj
    }
    fun placeOrder() {
//        productBarCode.value = barcode
        if (customer != null) {
            if(totalAmount<1 || orderItemList.size==0){
                setFlag(Flag(false,"Please Add products to place order"))

            }else
            uiScope.launch {

                val order =Order()
                order.orderId=orderId
                order.storeChainId =0
                order.orderNum=0
                order.orderDate =  Utils.getTodaysDate()
                order.customerId =customer!!.phone
                order.customerName=customer!!.name
                order.customerMobile=customer!!.phone
                order.customerAddress=customer!!.address
                order.orderAmount = totalAmount.toString()
                order.addedDate= Utils.getTodaysDate()
                orderItemRepository.insertOrderItems(orderItemList)
                orderRepository.insertOrder(order)
                setFlag(Flag(true,"Order placed successfully"))
            }
       }
        else{
            setFlag(Flag(false,"Please add Customer details"))

        }
    }



    fun addCustomer(customer: LocalCustomer){
        uiScope.launch {
            localCustomerRepository.insertLocalCustomer(customer)
            setFlag(Flag(false,"Customer added"))

        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}
