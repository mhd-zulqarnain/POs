package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StorePayoption {

    @SerializedName("payment_type")
    @Expose
    var paymentType: String? = null
    @SerializedName("payment_value")
    @Expose
    var paymentValue: String? = null

}
