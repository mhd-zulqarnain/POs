package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LocationDeliveryRegion {

    @SerializedName("type")
    @Expose
    var type: String? = null
    @SerializedName("country_id")
    @Expose
    var countryId: String? = null
    @SerializedName("country_flag")
    @Expose
    var countryFlag: String? = null
    @SerializedName("country_name")
    @Expose
    var countryName: String? = null
    @SerializedName("state_id")
    @Expose
    var stateId: String? = null
    @SerializedName("state_flag")
    @Expose
    var stateFlag: String? = null
    @SerializedName("state_name")
    @Expose
    var stateName: String? = null
    @SerializedName("city_id")
    @Expose
    var cityId: String? = null
    @SerializedName("city_flag")
    @Expose
    var cityFlag: String? = null
    @SerializedName("city_name")
    @Expose
    var cityName: String? = null
    @SerializedName("location_id")
    @Expose
    var locationId: String? = null
    @SerializedName("location_name")
    @Expose
    var locationName: String? = null
    @SerializedName("delivery_duration")
    @Expose
    var deliveryDuration: String? = null
    @SerializedName("delivery_unit")
    @Expose
    var deliveryUnit: String? = null

}
