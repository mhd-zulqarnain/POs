package com.goshoppi.pos.webservice

import com.goshoppi.pos.model.ProductSearchResponse
import com.goshoppi.pos.utilities.Constants
import com.goshoppi.pos.utilities.Utils
import okhttp3.*
import okio.Buffer
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by waqar.eid on 30-Oct-18.
 */

object ServiceHandler {


    var API_URL = WebServiceConstants.MERCHANT_QA_URL
    public lateinit var strRespon: String
    private var restService: RestService? = null

    //.header("apikey", API_KEY);
    val instance: RestService?
        get() {


            if (Constants.isDebug)
                this.API_URL = WebServiceConstants.MERCHANT_QA_URL
            else
                this.API_URL = WebServiceConstants.MERCHANT_PR_URL

            //this.API_URL = this.API_URL.replace("merchantservices","storeservices")//important

            if (restService != null) return restService

            val okHttpClient = OkHttpClient().newBuilder().addInterceptor { chain ->
                val originalRequest = chain.request()

                val builder = originalRequest.newBuilder()
                builder.addHeader("Content-Type", "application/json")
                //builder.addHeader("Authorization", generateHMAC())

                val newRequest = builder.build()
                val response = chain.proceed(newRequest)
                //strRespon = response.peekBody(Long.MAX_VALUE)
                logReqRes(newRequest, response)

                response
            }.connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .build()
            okHttpClient.connectTimeoutMillis()
            val retrofit = Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()

            restService = retrofit.create<RestService>(RestService::class.java!!)
            return restService
        }

    private fun logReqRes(req: Request, res: Response) {
        var str: String = ""
        try {
            /*var str:String = "REQUEST " + req.method().toString() + " " + req.url()
                    str + "\nHeaders: " + req.headers().toString()
                    str + "Body: " + bodyToString(req.body())*/

            var responseBodyCopy: ResponseBody = res.peekBody(Long.MAX_VALUE)

            str + "RESPONSE " + res.code() + "\nHeaders:" + res.headers().toString()

            Utils.WriteLogs("RESPONSE " + res.code() + "\nHeaders:" + res.headers().toString()
                    + "\nBody:" + responseBodyCopy.string())
            //str+ "\nBody:" + responseBodyCopy.string()

            /*var gson:Gson = Gson()

            var token:Token = gson.fromJson(responseBodyCopy.string(), Token::class.java!!)
            token.getExpires()*/

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun bodyToString(request: RequestBody?): String {
        try {
            val buffer = Buffer()
            if (request != null)
                request.writeTo(buffer)
            else
                return ""
            return buffer.readUtf8()
        } catch (e: IOException) {
            return e.toString()
        }

    }


    interface RestService {
        @FormUrlEncoded
        @POST("getStoreCatalog")
        fun getAllProducts(@Field("client_key") client_key: String, @Field("admin_id") admin_id: String, @Field("store_id") store_id: String, @Field("page") page: Int) : Call<ProductSearchResponse>


    }

}
