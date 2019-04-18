package com.goshoppi.pos.architecture.repository.masterProductRepo

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.PagedList
import com.goshoppi.pos.model.master.MasterProduct
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.annotation.NonNull



interface MasterProductRepository {

    fun loadAllMasterProduct(): LiveData<List<MasterProduct>>
    fun loadAllStaticMasterProduct(): List<MasterProduct>
    fun insertMasterProduct(product:MasterProduct )
    fun insertMasterProducts(productList:List<MasterProduct> )
    fun searchMasterProducts(param: String):LiveData<List<MasterProduct>>
    fun loadAllPaginatedMasterSearchProduct(param:String): DataSource.Factory<Int, MasterProduct>

}

