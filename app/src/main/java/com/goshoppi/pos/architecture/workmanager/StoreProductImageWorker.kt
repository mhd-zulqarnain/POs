package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository


import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import timber.log.Timber
import javax.inject.Inject

class StoreProductImageWorker(var context: Context, params: WorkerParameters) : Worker(context, params) {


    /*init {
        DaggerAppComponent.builder()
            .appModule(AppModule(context as Application))
            .roomModule(RoomModule(context as Application))
            .build()
            .injectStoreProductImageWorker(this)
    }*/

    @Inject
    lateinit var masterProductRepository: MasterProductRepository


    override fun doWork(): Result {
        downloadData()
        return Result.success()
    }

    private fun downloadData() {
           val products: List<MasterProduct> = masterProductRepository.loadAllStaticMasterProduct()
                products.forEach {prd->
                    prd.productImages!!.forEachIndexed { index, img ->
                        Utils.saveImage(img, "${prd.storeProductId}_$index", Constants.PRODUCT_IMAGE_DIR+prd.storeProductId)
                        Timber.e("Saving images")
                    }
                }
                Timber.e("StoreProductImageWorker downloadData Successfully")
    }
}