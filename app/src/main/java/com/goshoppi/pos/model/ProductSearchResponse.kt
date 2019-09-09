package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.goshoppi.pos.model.local.LocalProduct
import com.goshoppi.pos.model.master.MasterProduct


class ProductSearchResponse {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("data")
    @Expose
    var data: Data? = null
    @SerializedName("error")
    @Expose
    var error: String? = null

}

class Data {

    @SerializedName("total_products")
    @Expose
    var totalProducts: Int? = null
    @SerializedName("products")
    @Expose
    var products: List<LocalProduct>? = null

}




