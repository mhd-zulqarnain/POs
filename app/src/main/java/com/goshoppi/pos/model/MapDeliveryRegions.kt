package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MapDeliveryRegions {
    @SerializedName("area_title")
    @Expose
    var area_title: String? = null
    @SerializedName("delivery_duration")
    @Expose
    var delivery_duration: String? = null
    @SerializedName("delivery_unit")
    @Expose
    var delivery_unit: String? = null
    @SerializedName("coordinates")
    @Expose
    var coordinates: Array<String>? = null
}
