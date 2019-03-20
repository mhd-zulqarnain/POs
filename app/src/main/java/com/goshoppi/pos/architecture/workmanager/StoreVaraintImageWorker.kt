package com.goshoppi.pos.architecture.workmanager

import android.arch.lifecycle.LiveData
import android.content.Context
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.AppDatabase
import com.goshoppi.pos.model.Product
import com.goshoppi.pos.model.ProductSearchResponse
import com.goshoppi.pos.model.Variant
import com.goshoppi.pos.utils.Constants
import com.goshoppi.pos.utils.Utils
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

private const val TAG = "SyncWorker"

class StoreVaraintImageWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {

        val appDatabase: AppDatabase =
            AppDatabase.getInstance(context = context)
        downloadData(appDatabase)
        Timber.tag(TAG)
        Timber.e("Do Work")
        return Result.success()
    }


    fun downloadData(appDatabase: AppDatabase) {
        doAsync {
            val totalCount = appDatabase.productDao().countTotalProductSync0()

            Timber.e("totalCount $totalCount")
            val products: List<Product> = appDatabase.productDao().loadAllStaticProduct()
                products.forEach {prd->
                    var variants:List<Variant> = appDatabase.varaintDao().getVaraintsOfProducts(prd.storeProductId)
                    variants.forEach{varaint->
                        Utils.saveImage(varaint.productImage, varaint.storeRangeId, "${Constants.PRODUCT_IMAGE_DIR}${prd.storeProductId}//${Constants.VARAINT_IMAGE_DIR}")
                        Timber.e("Saving varaint images")
                    }
                }
            Timber.e("Insert Runs Successfully")


            uiThread {
                Toast.makeText(context, " varaint images Saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

}