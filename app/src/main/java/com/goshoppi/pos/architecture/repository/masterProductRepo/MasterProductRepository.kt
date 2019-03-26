package com.goshoppi.pos.architecture.repository.masterProductRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.model.master.MasterProduct

interface MasterProductRepository {

    fun loadAllMasterProduct(): LiveData<List<MasterProduct>>
    fun loadAllStaticMasterProduct(): List<MasterProduct>
    fun insertMasterProduct(product:MasterProduct )
    fun insertMasterProducts(productList:List<MasterProduct> )
    fun searchMasterProducts(param: String):LiveData<List<MasterProduct>>
}