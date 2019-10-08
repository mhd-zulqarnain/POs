package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.repository.masterProductRepo.MasterProductRepository
import com.goshoppi.pos.architecture.repository.masterVariantRepo.MasterVariantRepository
import com.goshoppi.pos.utils.SharedPrefs

import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.webservice.retrofit.MyServices
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import timber.log.Timber
import javax.inject.Inject

class SyncWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    /*init {
        DaggerAppComponent.builder()
            .appModule(AppModule(context as Application))
            .roomModule(RoomModule(context as Application))
            .build()
            .injectSyncWorker(this)
    }*/

    @Inject
    lateinit var masterProductRepository: MasterProductRepository
    @Inject
    lateinit var masterVariantRepository : MasterVariantRepository
    @Inject
    lateinit var myServices : MyServices

    override fun doWork(): Result {
        Utils.createNotification("Syncing Master Database in Progress", context,3)

        val store = SharedPrefs.getInstance()!!.getStoreDetails(context)
        val clint = store!!.clintKey
        val adminId = store.adminId
        val storeId = store.storeId
        getProductListFirst(clint, adminId, storeId, 1)
        return Result.success()
    }

    private fun getProductListFirst(
        clint: String?,
        adminId: String?,
        storeId: String?,
        page: Int
    ) {
        val response = RetrofitClient.
            getInstance()?.
            getService()?.
            getAllProducts(clint!!, adminId!!, storeId!!, page)!!
            .execute()

        if (response.isSuccessful) {
            if (response.body() != null) {
                if (response.body()?.status == true && response.body()?.code == 200) {
                    if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                        masterProductRepository.insertMasterProducts(response.body()?.data?.products!!)

                        val totalPrd = response.body()!!.data?.totalProducts!!.toInt()
                        val pages = Math.ceil(totalPrd.toDouble() / 25).toInt()
                        Timber.e("Do Syn Work Pages :$pages")

                        for (innerpage in 2..pages) {
                            getProductList(clint, adminId, storeId, innerpage)
                        }

                        response.body()?.data?.products!!.forEach {
                            it.variants.forEach {variant ->
                                variant.productId = it.storeProductId.toLong()
                                variant.productName = it.productName!!
                                masterVariantRepository.insertMasterVariant(variant)
                            }
                        }

                    }
                }
            }
        } else {
            Timber.e("response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}")
        }

    }

    private fun getProductList(
        clint: String?,
        adminId: String?,
        storeId: String?,
        page: Int
    ) {
        val response = RetrofitClient.
            getInstance()?.
            getService()?.
            getAllProducts(clint!!, adminId!!, storeId!!, page)!!
            .execute()

        if (response.isSuccessful) {
            if (response.body() != null) {
                if (response.body()?.status == true && response.body()?.code == 200) {
                    if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                        masterProductRepository.insertMasterProducts(response.body()?.data?.products!!)

                        response.body()?.data?.products!!.forEach {
                            it.variants.forEach {variant ->
                                variant.productId = it.storeProductId
                                variant.productName = it.productName!!
                                variant.categoryId = it.categoryId
                                variant.subcategoryId = it.subcategoryId
                                masterVariantRepository.insertMasterVariant(variant)
                            }
                        }

                    }
                }
            }
        } else {
            Timber.e("response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}")
        }

    }

}