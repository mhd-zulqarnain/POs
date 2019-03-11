package com.goshoppi.pos.architecture.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.repository.ProductRepository
import com.goshoppi.pos.model.Product


private const val TAG = "ProductViewModel"

class ProductViewModel(application: Application) : AndroidViewModel(application) {

    private var productRepository: ProductRepository = ProductRepository.getInstance(application)
    var listProductLiveData: LiveData<List<Product>>? = null
    var totalCount : LiveData<Int>? = null

    init {
        totalCount = productRepository.getTotalCount()
        listProductLiveData = productRepository.getAllProducts()
    }

    fun insertProduct(productList : List<Product>) {
        productRepository.insertProductList(productList)
    }
}
