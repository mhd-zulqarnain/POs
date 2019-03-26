package com.goshoppi.pos.architecture.repository.localProductRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.model.local.LocalProduct

interface LocalProductRepository {

    fun loadAllLocalProduct(): LiveData<List<LocalProduct>>
    fun insertLocalProduct(product:LocalProduct )
    fun insertLocalProducts(productList:List<LocalProduct> )
    fun searchLocalProducts(param: String):LiveData<List<LocalProduct>>
}