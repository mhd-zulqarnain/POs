package com.goshoppi.pos.architecture.repository.masterProductRepo

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.goshoppi.pos.model.master.MasterProduct
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.annotation.NonNull



interface MasterProductRepository {

    fun loadAllMasterProduct(): LiveData<List<MasterProduct>>
    fun loadAllStaticMasterProduct(): List<MasterProduct>
    fun insertMasterProduct(product:MasterProduct )
    fun insertMasterProducts(productList:List<MasterProduct> )
    fun searchMasterProducts(param: String):LiveData<List<MasterProduct>>
    fun loadAllPaginatedMasterSearchProduct(param:String): DataSource.Factory<Int, MasterProduct>


}

