package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StoreBanner {

    @SerializedName("banner_id")
    @Expose
    var bannerId: String? = null
    @SerializedName("image_name")
    @Expose
    var imageName: String? = null

}
