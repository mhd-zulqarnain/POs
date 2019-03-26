package com.goshoppi.pos.architecture.workmanager

import android.app.Application
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.di.component.DaggerAppComponent
import com.goshoppi.pos.di.module.AppModule
import com.goshoppi.pos.di.module.RoomModule
import com.goshoppi.pos.model.master.MasterVariant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.TinyDB
import com.goshoppi.pos.utils.Utils
import timber.log.Timber
import javax.inject.Inject

class StoreVariantImageWorker(private var context: Context, var params: WorkerParameters) : Worker(context, params) {

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(context as Application))
            .roomModule(RoomModule(context as Application))
            .build()
            .injectStoreVariantImageWorker(this)
    }

    @Inject
    lateinit var masterProductRepository : MasterProductRepository

    @Inject
    lateinit var masterVariantRepository : MasterVariantRepository

    override fun doWork(): Result {

        downloadData()
        Timber.e("Do Work Variant")
        return Result.success()
    }

    private fun downloadData() {
       val products= masterProductRepository.loadAllStaticMasterProduct()
        products.forEach {  prd ->
            val variants: List<MasterVariant> = masterVariantRepository.getMasterStaticVariantsOfProducts(prd.storeProductId)
            variants.forEach { varaint ->
                Utils.saveImage(
                    varaint.productImage,
                    varaint.storeRangeId,
                    "${Constants.PRODUCT_IMAGE_DIR}${prd.storeProductId}//${Constants.VARAINT_IMAGE_DIR}"
                )
                Timber.e("Saving varaint images")
            }

        }
        val tinyDb = TinyDB(context)
        tinyDb.putBoolean(Constants.MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY,true)
        Timber.e("StoreVariantImageWorker downloadData Successfully")
        Utils.createNotification("Syncing completed",context)

    }
}