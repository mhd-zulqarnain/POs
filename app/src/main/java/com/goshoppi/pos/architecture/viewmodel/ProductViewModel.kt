package com.goshoppi.pos.architecture.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.repository.ProductRepository
import com.goshoppi.pos.model.master.MasterProduct

class ProductViewModel(application: Application) : AndroidViewModel(application) {

     var productRepository: ProductRepository = ProductRepository.getInstance(application)
    var listProductLiveData: LiveData<List<MasterProduct>>? = null
    var totalCount : LiveData<Int>? = null

    init {
        totalCount = productRepository.getTotalCount()
        listProductLiveData = productRepository.getAllMasterProducts()
    }

    fun insertProduct(productList : List<MasterProduct>) {
        productRepository.insertMasterProductList(productList)
    }
    fun searhMasterProduct(param : String) {
        productRepository.searhMasterProduct(param)
    }
}
