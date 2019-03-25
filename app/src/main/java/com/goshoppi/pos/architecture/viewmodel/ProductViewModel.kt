package com.goshoppi.pos.architecture.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.repository.ProductRepository
import com.goshoppi.pos.model.master.MasterProduct
import javax.inject.Inject
import android.arch.lifecycle.ViewModel



/*
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
*/

class ProductViewModel// Instructs Dagger 2 to provide the UserRepository parameter.
@Inject
constructor(private val productRepo: ProductRepository) : ViewModel() {

    fun getMasterProduct(){
        productRepo.getAllMasterProducts()
    }

    fun searhMasterProduct(param: String): LiveData<List<MasterProduct>>? {
        return productRepo.searhMasterProduct(param)
    }

}