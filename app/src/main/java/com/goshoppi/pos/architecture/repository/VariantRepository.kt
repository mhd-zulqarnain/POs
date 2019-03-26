package com.goshoppi.pos.architecture.repository

import android.content.Context
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterVariant
import org.jetbrains.anko.doAsync
import timber.log.Timber

class VariantRepository private constructor() {

    private object SingletonHelper {
        val INSTANCE = VariantRepository()
    }

    /*companion object {
        private var appDatabase: AppDatabase? = null

        fun getInstance(context: Context): VariantRepository {
            appDatabase = AppDatabase.getInstance(context)
            return VariantRepository.SingletonHelper.INSTANCE
        }
    }

    fun getVariantsOfProductsById(productId: String): List<MasterVariant>? {
        Timber.e("getVariantsOfProductsById $productId")
        Timber.e("getVariantsOfProductsById Size ${appDatabase?.varaintDao()?.getVariantsOfProducts(productId)?.size}")
        return appDatabase?.varaintDao()?.getVariantsOfProducts(productId)
    }
    fun insertLocalVaraint(varaint: LocalVariant) {
        doAsync {
            VariantRepository.appDatabase!!.varaintDao().insertLocalVaraint(varaint)
        }
    }*/
}