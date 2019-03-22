package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.master.MasterProduct
import com.goshoppi.pos.model.ProductSearchResponse
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class SyncWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        Utils.createSyncNotifier("Syncing Master Database in Progress", context)

        val appDatabase: AppDatabase =
            AppDatabase.getInstance(context = context)
        getProductList(appDatabase)
        Timber.e("Do Syn Work")
        return Result.success()
    }

    private fun getProductList(appDatabase: AppDatabase) {
        RetrofitClient.getInstance()?.getService()?.getAllProducts("goshoppi777", "26", "22", 1)!!
            .enqueue(object : Callback<ProductSearchResponse> {
                override fun onResponse(call: Call<ProductSearchResponse>, response: Response<ProductSearchResponse>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.body()?.status == true && response.body()?.code == 200) {
                                if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                                    downloadData(appDatabase, response.body()?.data?.products!!)

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

                override fun onFailure(call: Call<ProductSearchResponse>, t: Throwable) {
                    Timber.e("t.message ${t.message}")
                    Timber.e("t.localizedMessage ${t.localizedMessage}")
                    Timber.e("t.stackTrace ${t.stackTrace}")
                    Timber.e("t.cause ${t.cause}")
                }
            })
    }

   private fun downloadData(appDatabase: AppDatabase, products: List<MasterProduct>) {
            val totalCount = appDatabase.productDao().countTotalProductSync0()
            Timber.e("totalCount $totalCount")
            Timber.e("products.size ${products.size}")
            if (totalCount != products.size) {
                products.forEach {prd->
                    appDatabase.productDao().insertProduct(prd)
                    prd.variants.forEach{varaint->
                        varaint.productId = prd.storeProductId
                        appDatabase.varaintDao().insertVaraint(varaint)
                    }
                }
                Timber.e("SyncWorker Insert Runs Successfully")
            } else {
                Timber.e("No need to Insert")
            }
    }
}