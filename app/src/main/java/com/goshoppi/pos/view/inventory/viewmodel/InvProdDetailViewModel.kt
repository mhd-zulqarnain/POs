package com.goshoppi.pos.view.inventory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

class InvProdDetailViewModel @Inject constructor(
    var masterVariantRepository: MasterVariantRepository,
    var localVariantRepository: LocalVariantRepository,
    var localProductRepository: LocalProductRepository
) : ViewModel() {

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    private val masterVariantMutableLiveDataList = MutableLiveData<List<MasterVariant>>()
    val masterVariantLiveDataList: LiveData<List<MasterVariant>>
        get() = masterVariantMutableLiveDataList

    fun getMasterVariantListByID(id: Long) {
        uiScope.launch {
            masterVariantMutableLiveDataList.value = masterVariantRepository.getMasterVariantsByProductId(id)
        }
    }

    fun insertLocalProductAndItsVariants(masterProduct: MasterProduct, masterVariantList: ArrayList<MasterVariant>) {
        uiScope.launch {
            /*saving product to local database*/
            val mJson = Gson().toJson(masterProduct)
            val localProduct: LocalProduct = Gson().fromJson(mJson, LocalProduct::class.java)
            localProductRepository.insertLocalProduct(localProduct)

            /*saving variants to local database*/
            masterVariantList.forEach {
                val json = Gson().toJson(it)
                val variant: LocalVariant = Gson().fromJson(json, LocalVariant::class.java)
                localVariantRepository.insertLocalVariant(variant)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}