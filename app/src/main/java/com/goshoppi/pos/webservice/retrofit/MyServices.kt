package com.goshoppi.pos.webservice.retrofit

import com.goshoppi.pos.model.LoginResponse
import com.goshoppi.pos.model.ProductSearchResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyServices {

    @FormUrlEncoded
    @POST("getStoreCatalog")
    fun getAllProducts(@Field("client_key") client_key: String,
                       @Field("admin_id") admin_id: String,
                       @Field("store_id") store_id: String,
                       @Field("page") page: Int) : Call<ProductSearchResponse>

    @FormUrlEncoded
    @POST("validateStore")
    fun getvalidateStore(
        @Field("client_key") client_key: String,
        @Field("email_id") email_id: String,
        @Field("password") store_id: String
    ):
            Call<LoginResponse>


}