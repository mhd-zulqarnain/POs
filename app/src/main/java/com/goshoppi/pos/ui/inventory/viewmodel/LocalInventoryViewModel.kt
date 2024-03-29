package com.goshoppi.pos.ui.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import kotlinx.coroutines.*
import timber.log.Timber
import javax.inject.Inject

class LocalInventoryViewModel @Inject constructor(
    var localProductRepository: LocalProductRepository,
    var localVariantRepository: LocalVariantRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    /** LocalInventoryActivity Local Product Work START*/
    var localProductLiveDataList: LiveData<List<LocalProduct>> = localProductRepository.loadAllLiveLocalProduct()
    var productSearchParam: MutableLiveData<String> = MutableLiveData()

    var searchedLocalProductList: LiveData<List<LocalProduct>> =
        Transformations.switchMap(productSearchParam) { param ->
            Timber.e("The result $param")
            localProductRepository.searchLocalProducts(param)
        }

    fun getAllLocalProduct(): List<LocalProduct> = runBlocking {
        localProductRepository.loadAllStaticLocalProduct()
    }

    fun insertLocalProductList(list: ArrayList<LocalProduct>) {
        uiScope.launch {
            localProductRepository.insertLocalProducts(list)
        }
    }

    fun deleteLocalProduct(prdId: Long) {
        uiScope.launch {
            localProductRepository.deleteLocalProducts(prdId)
        }
    }

    fun search(param: String) {
        productSearchParam.value = param
    }
    /** LocalInventoryActivity Local Product Work END*/


    /** LocalInventoryActivity Local Product Variants Work START*/

    fun getAllLocalProductVariants(): List<LocalVariant> = runBlocking {
        localVariantRepository.loadAllStaticLocalVariants()
    }

    fun getLocalProductVariantById(prdId: Long): List<Long> = runBlocking {
        localVariantRepository.getStaticVaraintIdList(prdId)
    }


    fun deleteLocalProductVariants(listId: List<Long>) {
        uiScope.launch {
            localVariantRepository.deleteVaraint(listId)
        }
    }

    fun deleteLocalProductVariant(prdId: Long) {
        uiScope.launch {
            localVariantRepository.deleteVaraint(prdId)
        }
    }

    fun insertLocalProductVariants(variantsList: List<LocalVariant>) {
        uiScope.launch {
            localVariantRepository.insertLocalVariants(variantsList)
        }
    }

    fun getAllLocalProductVariantsById(prdId: Long): LiveData<List<LocalVariant>> = runBlocking {
        localVariantRepository.getLocalVariantsByProductId(prdId)
    }

    /** LocalInventoryActivity Local Product Variants Work END*/
    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }
}