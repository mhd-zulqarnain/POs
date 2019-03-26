package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import timber.log.Timber

class StoreVariantImageWorker(private var context: Context, var params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

       /* val appDatabase: AppDatabase = AppDatabase.getInstance(context = context)
        val repo = LocalProductRepositoryImpl.getInstance(context)
        downloadData(repo)*/
        Timber.e("Do Work Variant")
        return Result.success()
    }
//
//    private fun downloadData(repo: LocalProductRepositoryImpl) {
//        val products: List<MasterProduct> = repo.getAllProducts() as ArrayList
//
//        products.forEachIndexed { index, prd ->
//            val variants: List<MasterVariant> = repo.getVariantsOfProducts(prd.storeProductId)
//            variants.forEach { varaint ->
//                Utils.saveImage(
//                    varaint.productImage,
//                    varaint.storeRangeId,
//                    "${Constants.PRODUCT_IMAGE_DIR}${prd.storeProductId}//${Constants.VARAINT_IMAGE_DIR}"
//                )
//                Timber.e("Saving varaint images")
//            }
//           /* if (index == products.size - 1) {
//                val tinyDb = TinyDB(context)
//                tinyDb.putBoolean(Constants.MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY, true)
//                Utils.createNotification("Syncing Master Database in Terminal is completed", context)
//            }*/
//        }
//        Timber.e("StoreVariantImageWorker downloadData Successfully")
//
//    }
}