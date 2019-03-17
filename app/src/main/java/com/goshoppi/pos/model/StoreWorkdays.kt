package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StoreWorkdays {

    @SerializedName("monday")
    @Expose
    var monday: String? = null
    @SerializedName("monday_open")
    @Expose
    var mondayOpen: String? = null
    @SerializedName("monday_close")
    @Expose
    var mondayClose: String? = null
    @SerializedName("tuesday")
    @Expose
    var tuesday: String? = null
    @SerializedName("tuesday_open")
    @Expose
    var tuesdayOpen: String? = null
    @SerializedName("tuesday_close")
    @Expose
    var tuesdayClose: String? = null
    @SerializedName("wednesday")
    @Expose
    var wednesday: String? = null
    @SerializedName("wednesday_open")
    @Expose
    var wednesdayOpen: String? = null
    @SerializedName("wednesday_close")
    @Expose
    var wednesdayClose: String? = null
    @SerializedName("thursday")
    @Expose
    var thursday: String? = null
    @SerializedName("thursday_open")
    @Expose
    var thursdayOpen: String? = null
    @SerializedName("thursday_close")
    @Expose
    var thursdayClose: String? = null
    @SerializedName("friday")
    @Expose
    var friday: String? = null
    @SerializedName("friday_open")
    @Expose
    var fridayOpen: String? = null
    @SerializedName("friday_close")
    @Expose
    var fridayClose: String? = null
    @SerializedName("saturday")
    @Expose
    var saturday: String? = null
    @SerializedName("saturday_open")
    @Expose
    var saturdayOpen: String? = null
    @SerializedName("saturday_close")
    @Expose
    var saturdayClose: String? = null
    @SerializedName("sunday")
    @Expose
    var sunday: String? = null
    @SerializedName("sunday_open")
    @Expose
    var sundayOpen: String? = null
    @SerializedName("sunday_close")
    @Expose
    var sundayClose: String? = null

}
