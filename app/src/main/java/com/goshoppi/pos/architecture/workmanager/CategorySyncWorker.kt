package com.goshoppi.pos.architecture.workmanager

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.goshoppi.pos.architecture.repository.localProductRepo.LocalProductRepository
import com.goshoppi.pos.model.StoreCategoryResponse
import com.goshoppi.pos.webservice.retrofit.RetrofitClient
import timber.log.Timber
import javax.inject.Inject

class CategorySyncWorker(private var context: Context, params: WorkerParameters) : Worker(context, params) {

    @Inject
    lateinit var localProductRepository: LocalProductRepository

    override fun doWork(): Result {
        getStoreCategories()
        return Result.success()
    }


    private fun getStoreCategories() {
        val response = RetrofitClient.getInstance()?.getService()?.getCategory("goshoppi777", "22")!!
            .execute()

        if (response.isSuccessful) {
            if (response.body() != null) {
                val storeCategoryResponse = response.body() as StoreCategoryResponse
                val storeCategoryList = storeCategoryResponse.storeCategories
               if(storeCategoryList!=null) {
                   localProductRepository.insertStoreCategories(storeCategoryList)

                   storeCategoryList.forEach {
                       if(it.subCategories!=null){
                           it.subCategories!!.forEach{sub->
                               sub.categoryId=it.categoryId
                               localProductRepository.insertSubCategory(sub)

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