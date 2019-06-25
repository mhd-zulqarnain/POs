package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class StoreCategoryResponse {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("data")
    @Expose
    var storeCategories: List<StoreCategory>? = null

}
