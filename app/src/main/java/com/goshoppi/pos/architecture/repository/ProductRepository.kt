package com.goshoppi.pos.architecture.repository

import android.arch.lifecycle.LiveData
import android.content.Context
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.Product
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

    fun getAllProducts(): LiveData<List<Product>>? {
        return appDatabase!!.productDao().loadAllProduct()
    }

    fun getTotalCount(): LiveData<Int>? {
        return appDatabase!!.productDao().countTotalProduct()
    }

    fun insertProductList(productList: List<Product>) {
        doAsync {
            productList.forEach {
                appDatabase!!.productDao().insertProduct(it)
            }
        }
    }

    fun searhMasterProduct(param: String): List<Product>? {

        return appDatabase!!.productDao().getSearchResult(param)
    }


}