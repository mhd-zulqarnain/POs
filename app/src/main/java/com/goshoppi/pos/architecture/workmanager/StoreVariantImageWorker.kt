package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.model.local.LocalVariant


import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.TinyDB
import com.goshoppi.pos.utils.Utils
import timber.log.Timber
import javax.inject.Inject

class StoreVariantImageWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    /*init {
        DaggerAppComponent.builder()
            .appModule(AppModule(context as Application))
            .roomModule(RoomModule(context as Application))
            .build()
            .injectStoreVariantImageWorker(this)
    }*/


    @Inject
    lateinit var localProductRepository: LocalProductRepository

    @Inject
    lateinit var localVariantRepository: LocalVariantRepository



    override fun doWork(): Result {

        downloadData()
        Timber.e("Do Work Variant")
        return Result.success()
    }

    private fun downloadData() {
       val products= localProductRepository.loadAllLocalProduct()
        products.forEach {  prd ->
            val variants: List<LocalVariant> = localProductRepository.getMasterStaticVariantsOfProductsWorkManager(prd.storeProductId)
            variants.forEach { varaint ->
                Utils.saveImage(
                    varaint.productImage!!,
                    varaint.storeRangeId.toString(),
                    "${Constants.PRODUCT_IMAGE_DIR}${prd.storeProductId}//${Constants.VARIANT_IMAGE_DIR}"
                )
                Timber.e("Saving varaint images")
            }

        }
        val tinyDb = TinyDB(context)
        tinyDb.putBoolean(Constants.MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY,true)
        Timber.e("StoreVariantImageWorker downloadData Successfully")
        Utils.createNotification("Syncing completed",context,0)

    }
}