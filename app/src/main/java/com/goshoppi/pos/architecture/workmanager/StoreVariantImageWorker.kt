package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.model.Variant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import timber.log.Timber

class StoreVariantImageWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        val appDatabase: AppDatabase = AppDatabase.getInstance(context = context)
        downloadData(appDatabase)
        Timber.e("Do Work")
        return Result.success()
    }

    private fun downloadData(appDatabase: AppDatabase) {
        val totalCount = appDatabase.productDao().countTotalProductSync0()
        Timber.e("totalCount $totalCount")
        val products: List<Product> = appDatabase.productDao().loadAllStaticProduct()
        products.forEach { prd ->
            val variants: List<Variant> = appDatabase.varaintDao().getVariantsOfProducts(prd.storeProductId)
            variants.forEach { varaint ->
                Utils.saveImage(
                    varaint.productImage,
                    varaint.storeRangeId,
                    "${Constants.PRODUCT_IMAGE_DIR}${prd.storeProductId}//${Constants.VARAINT_IMAGE_DIR}"
                )
                Timber.e("Saving varaint images")
            }
        }
        Timber.e("Insert Runs Successfully")
    }
}