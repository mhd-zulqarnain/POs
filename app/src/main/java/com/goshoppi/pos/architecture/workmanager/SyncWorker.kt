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
import com.goshoppi.pos.model.ProductSearchResponse
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class SyncWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    init {
        DaggerAppComponent.builder()
            .appModule(AppModule(context as Application))
            .roomModule(RoomModule(context as Application))
            .build()
            .injectSyncWorker(this)
    }

    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var masterVariantRepository : MasterVariantRepository

    override fun doWork(): Result {
        Utils.createSyncNotifier("Syncing Master Database in Progress", context)

        getProductListE()

        Timber.e("Do Syn Work")
        return Result.success()
    }



    private fun getProductListE() {
        val response = RetrofitClient.getInstance()?.getService()?.getAllProducts("goshoppi777", "26", "22", 1)!!
            .execute()

        if (response.isSuccessful) {
            if (response.body() != null) {
                if (response.body()?.status == true && response.body()?.code == 200) {
                    if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                        masterProductRepository.insertMasterProducts(response.body()?.data?.products!!)

                        response.body()?.data?.products!!.forEach {
                            it.variants.forEach {variant ->
                                variant.productId = it.storeProductId
                                masterVariantRepository.insertMasterVariant(variant)
                            }
                        }

                    } else {
                        Timber.e("response.body()?.status ${response.body()?.status}")
                        Timber.e("response.body()?.code == 200 ${response.body()?.code}")
                    }
                } else {
                    Timber.e("response.body()?.status ${response.body()?.status}")
                    Timber.e("response.body()?.code == 200 ${response.body()?.code}")
                }
            } else {
                Timber.e("response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}")
            }
        } else {
            Timber.e("response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}")
        }

    }
}