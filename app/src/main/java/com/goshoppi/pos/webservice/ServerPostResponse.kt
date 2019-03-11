package com.goshoppi.pos.webservice

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ServerPostResponse {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("message")
    @Expose
    var message: String? = null
    @SerializedName("error")
    @Expose
    var error: String? = null
    @SerializedName("data")
    @Expose
    var data: String? = null

}
