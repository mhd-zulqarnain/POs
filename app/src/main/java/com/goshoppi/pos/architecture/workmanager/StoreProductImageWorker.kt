package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import timber.log.Timber

class StoreProductImageWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val appDatabase: AppDatabase =
            AppDatabase.getInstance(context = context)
        downloadData(appDatabase)
        Timber.e("Do Work")
        return Result.success()
    }

    private fun downloadData(appDatabase: AppDatabase) {
            val totalCount = appDatabase.productDao().countTotalProductSync0()
            Timber.e("totalCount $totalCount")
            val products: List<Product> = appDatabase.productDao().loadAllStaticProduct()
                products.forEach {prd->
                    prd.productImages.forEachIndexed { index, img ->
                        Utils.saveImage(img, "${prd.storeProductId}_$index", Constants.PRODUCT_IMAGE_DIR+prd.storeProductId)
                        Timber.e("Saving images")
                    }
                }
                Timber.e("Insert Runs Successfully")
    }
}