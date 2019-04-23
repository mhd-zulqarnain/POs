package com.goshoppi.pos.architecture.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.orderItemRepo.OrderItemRepository
import com.goshoppi.pos.architecture.repository.orderRepo.OrderRepository
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.Order
import com.goshoppi.pos.model.OrderItem
import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalProduct
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AppScoped
class PosMainViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository,
    var orderRepository: OrderRepository,
    var orderItemRepository: OrderItemRepository
) : ViewModel() {

    val viewModelJob = Job()
    val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var productBarCode: MutableLiveData<String> = MutableLiveData()
    var customer: LocalCustomer? = null
    var orderItemList: ArrayList<OrderItem> = ArrayList()
    var totalAmount = 0.00

    var items: LiveData<LocalProduct> = Transformations.switchMap(productBarCode) { barcode ->
        localProductRepository.getProductByBarCode(barcode)
    }

    init {
        productBarCode.value = ""
    }

    fun search(barcode: String) {
        productBarCode.value = barcode

    }

    fun placeOrder(productlist: List<LocalProduct>) {
//        productBarCode.value = barcode
        if (customer != null) {
            uiScope.launch {
                productlist.forEach{order->
                    val orderItem = OrderItem()
                    orderItem.orderId
                    orderItem.productId
                    orderItem.mrp
                    orderItem.totalPrice
                    orderItem.taxAmount
                    orderItem.addedDate
                    orderItem.delFlag
                }
            }
        }
    }


}
