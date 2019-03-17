package com.goshoppi.pos.webservice.retrofit

import com.goshoppi.pos.utils.Constants
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by MuhammadFarhan on 18/07/2018.
 */
class RetrofitClient private constructor() {

    private var mRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var myServices: MyServices

    companion object {

        private var retrofitClient: RetrofitClient? = null

        fun getInstance(): RetrofitClient? {
            if (retrofitClient == null) {
                retrofitClient = RetrofitClient()
            }
            return retrofitClient
        }
    }
    init {
        myServices = mRetrofit.create(MyServices::class.java)
    }
    fun getService(): MyServices {
        return myServices
    }
}