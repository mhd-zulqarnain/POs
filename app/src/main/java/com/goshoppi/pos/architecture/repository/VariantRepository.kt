package com.goshoppi.pos.architecture.repository

import android.content.Context
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.Variant
import timber.log.Timber

class VariantRepository private constructor() {

    private object SingletonHelper {
        val INSTANCE = VariantRepository()
    }

    companion object {
        private var appDatabase: AppDatabase? = null

        fun getInstance(context: Context): VariantRepository {
            appDatabase = AppDatabase.getInstance(context)
            return VariantRepository.SingletonHelper.INSTANCE
        }
    }

    fun getVariantsOfProductsById(productId: String): List<Variant>? {
        Timber.e("getVariantsOfProductsById $productId")
        Timber.e("getVariantsOfProductsById Size ${appDatabase?.varaintDao()?.getVariantsOfProducts(productId)?.size}")
        return appDatabase?.varaintDao()?.getVariantsOfProducts(productId)
    }
}