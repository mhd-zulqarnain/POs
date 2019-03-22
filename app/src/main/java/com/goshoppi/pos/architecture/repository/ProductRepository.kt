package com.goshoppi.pos.architecture.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant
import org.jetbrains.anko.doAsync

class ProductRepository private constructor() {

    private object SingletonHelper {
        val INSTANCE = ProductRepository()
    }

    companion object {
        private var appDatabase: AppDatabase? = null

        fun getInstance(context: Context): ProductRepository {
            appDatabase = AppDatabase.getInstance(context)
            return ProductRepository.SingletonHelper.INSTANCE
        }
    }

    fun getAllMasterProducts(): LiveData<List<MasterProduct>>? {
        return appDatabase!!.productDao().loadAllProduct()
    }
    fun getTotalCount(): LiveData<Int>? {
        return appDatabase!!.productDao().countTotalProduct()
    }

    fun insertMasterProductList(productList: List<MasterProduct>) {
        doAsync {
            productList.forEach {
                appDatabase!!.productDao().insertProduct(it)
            }
        }
    }
    fun insertLocalProduct(product: LocalProduct) {
        doAsync {
                appDatabase!!.productDao().insertLocalProduct(product)
        }
    }

    fun searhMasterProduct(param: String): List<MasterProduct>? {

        return appDatabase!!.productDao().getSearchResult(param)
    }


    fun getAllLocalProducts(): List<LocalProduct>? {
        return appDatabase!!.productDao().loadLocalAllStaticProduct()
    }

    fun searhLocalProduct(param: String): List<LocalProduct>? {

        return appDatabase!!.productDao().getLocalSearchResult(param)
    }


    fun getAllProducts(): List<MasterProduct> {
        return appDatabase!!.productDao().loadAllStaticProduct()
    }

    fun getVariantsOfProducts(param:String): List<MasterVariant> {
        return appDatabase!!.varaintDao().getVariantsOfProducts(param)
    }


}