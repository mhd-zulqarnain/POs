package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.model.ProductSearchResponse
import com.goshoppi.pos.webservice.ServiceHandler
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val TAG = "SyncWorker"

class SyncWorker(var context: Context, var params: WorkerParameters) : Worker(context, params) {


    override fun doWork(): Result {

        val appDatabase: AppDatabase =
            AppDatabase.getInstance(context = context)
        getProductList(appDatabase)

        Log.e(TAG, "Do Work")
        return Result.success()
    }

    private fun getProductList(appDatabase: AppDatabase) {
        ServiceHandler.instance!!.getAllProducts("goshoppi777", "26", "22", 1)
            .enqueue(object : Callback<ProductSearchResponse> {
                override fun onResponse(call: Call<ProductSearchResponse>, response: Response<ProductSearchResponse>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.body()?.status == true && response.body()?.code == 200) {
                                if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                                    downloadData(appDatabase, response.body()?.data?.products!!)

                                } else {
                                    Log.e(TAG, "response.body()?.status ${response.body()?.status}")
                                    Log.e(TAG, "response.body()?.code == 200 ${response.body()?.code}")
                                }
                            } else {
                                Log.e(TAG, "response.body()?.status ${response.body()?.status}")
                                Log.e(TAG, "response.body()?.code == 200 ${response.body()?.code}")
                            }
                        } else {
                            Log.e(
                                TAG,
                                "response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}"
                            )
                        }
                    } else {
                        Log.e(
                            TAG,
                            "response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ProductSearchResponse>, t: Throwable) {
                    Log.e(TAG, "t.message ${t.message}")
                    Log.e(TAG, "t.localizedMessage ${t.localizedMessage}")
                    Log.e(TAG, "t.stackTrace ${t.stackTrace}")
                    Log.e(TAG, "t.cause ${t.cause}")
                }
            })
    }

    private fun getProductLista(appDatabase: AppDatabase) {
        RetrofitClient.getInstance()?.getService()?.getAllProducts("goshoppi777", "26", "22", 1)!!
            .enqueue(object : Callback<ProductSearchResponse> {
                override fun onResponse(call: Call<ProductSearchResponse>, response: Response<ProductSearchResponse>) {
                    if (response.isSuccessful) {
                        if (response.body() != null) {
                            if (response.body()?.status == true && response.body()?.code == 200) {
                                if (response.body()!!.data?.totalProducts != 0 && response.body()!!.data?.products!!.isNotEmpty()) {

                                    downloadData(appDatabase, response.body()?.data?.products!!)

                                } else {
                                    Log.e(TAG, "response.body()?.status ${response.body()?.status}")
                                    Log.e(TAG, "response.body()?.code == 200 ${response.body()?.code}")
                                }
                            } else {
                                Log.e(TAG, "response.body()?.status ${response.body()?.status}")
                                Log.e(TAG, "response.body()?.code == 200 ${response.body()?.code}")
                            }
                        } else {
                            Log.e(
                                TAG,
                                "response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}"
                            )
                        }
                    } else {
                        Log.e(
                            TAG,
                            "response is null, Message:${response.message()} ErrorBody:${response.errorBody()} Code:${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<ProductSearchResponse>, t: Throwable) {
                    Log.e(TAG, "t.message ${t.message}")
                    Log.e(TAG, "t.localizedMessage ${t.localizedMessage}")
                    Log.e(TAG, "t.stackTrace ${t.stackTrace}")
                    Log.e(TAG, "t.cause ${t.cause}")
                }
            })
    }

    fun downloadData(appDatabase: AppDatabase, products: List<Product>) {
        doAsync {
            val totalCount = appDatabase.productDao().countTotalProductSync0()

            Log.e(TAG, "totalCount $totalCount")
            Log.e(TAG, "products.size ${products.size}")

            if (totalCount != products.size) {
                products.forEach {
                    appDatabase.productDao().insertProduct(it)
                }
                Log.e(TAG, "Insert Runs Successfully")
            } else {
                Log.e(TAG, "No need to Insert")
            }
            uiThread {
                Toast.makeText(context, "Result is Inserted from Work Manager", Toast.LENGTH_SHORT).show()
            }
        }
    }

}