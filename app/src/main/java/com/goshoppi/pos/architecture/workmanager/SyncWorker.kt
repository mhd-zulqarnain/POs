package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.architecture.repository.localVariantRepo.LocalVariantRepository
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.master.MasterVariant
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
    lateinit var localProductRepository: LocalProductRepository
    @Inject
    lateinit var localVariantRepository: LocalVariantRepository
    @Inject
    lateinit var myServices: MyServices

    override fun doWork(): Result {
        Utils.createNotification("Syncing Master Database in Progress", context, 3)

        val store = SharedPrefs.getInstance()!!.getStoreDetails(context)
        val clint = store!!.clintKey
        val adminId = store.adminId
        val storeId = store.storeId
        var page = 3
        getProductListFirst(clint, adminId, storeId, 1)
        Timber.e("Do Syn  params :$clint :$adminId :$storeId ")

        return Result.success()
    }

    private fun getProductList(
        clint: String?,
        adminId: String?,
        storeId: String?,
        page: Int
    ) {

        val response = RetrofitClient.getInstance()?.getService()?.getAllProducts(clint!!, adminId!!, storeId!!, page)!!
            .execute()

        if (response.isSuccessful) {
            if (response.body() != null) {
                if (response.body()?.status == true && response.body()?.code == 200) {
                    if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                        localProductRepository.insertStaticLocalProducts(response.body()?.data?.products!!)
                        response.body()?.data?.products!!.forEach {prd->
                            prd.variants.forEach {
                                it.productName = prd.productName!!
                            }
                        }
                        response.body()?.data?.products!!.forEach {

                            localVariantRepository.insertStaticLocalVariants(it.variants)
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

    private fun getProductListFirst(
        clint: String?,
        adminId: String?,
        storeId: String?,
        page: Int
    ) {

        val response = RetrofitClient.getInstance()?.getService()?.getAllProducts(clint!!, adminId!!, storeId!!, page)!!
            .execute()

        if (response.isSuccessful) {
            if (response.body() != null) {
                if (response.body()?.status == true && response.body()?.code == 200) {
                    if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                        val totalPrd = response.body()!!.data?.totalProducts!!.toInt()
                        val pages = Math.ceil(totalPrd.toDouble() / 25).toInt()
                        Timber.e("Do Syn Work Pages :$pages")

                        for (page in 2..pages) {
                            getProductList(clint, adminId, storeId, page)
                        }
                        localProductRepository.insertStaticLocalProducts(response.body()?.data?.products!!)
                        response.body()?.data?.products!!.forEach {prd->
                            prd.variants.forEach {
                              it.productName = prd.productName!!
                          }
                        }

                        response.body()?.data?.products!!.forEach {
                            localVariantRepository.insertStaticLocalVariants(it.variants)
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

    fun getLoclProduct(masterProduct: MasterProduct): LocalProduct {


        val json = Gson().toJson(masterProduct)

        return Gson().fromJson(json, LocalProduct::class.java)
    }

    fun getLoclVariant(masterVariant: MasterVariant): LocalVariant {


        val json = Gson().toJson(masterVariant)

        return Gson().fromJson(json, LocalVariant::class.java)
    }
}