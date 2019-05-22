package com.goshoppi.pos.model

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LoginResponse {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("data")
    @Expose
    var adminData: AdminData? = null
    @SerializedName("error")
    @Expose
    var error: String? = null

}

@Entity(tableName = "login_data")
class AdminData {
    @NonNull
    @PrimaryKey
    @SerializedName("admin_id")
    @Expose
    var adminId: String? = null
    @SerializedName("admin_name")
    @Expose
    var adminName: String? = null
    @SerializedName("admin_email")
    @Expose
    var adminEmail: String? = null
    @SerializedName("store_id")
    @Expose
    var storeId: String? = null
    @SerializedName("store_name")
    @Expose
    var storeName: String? = null
    @SerializedName("store_logo")
    @Expose
    var storeLogo: String? = null
    @SerializedName("store_shopiurl")
    @Expose
    var storeShopiurl: String? = null
    @SerializedName("business_type")
    @Expose
    var businessType: String? = null
    @SerializedName("store_web")
    @Expose
    var storeWeb: String? = null
    @SerializedName("store_add1")
    @Expose
    var storeAdd1: String? = null
    @SerializedName("store_add2")
    @Expose
    var storeAdd2: String? = null
    @SerializedName("store_lat")
    @Expose
    var storeLat: String? = null
    @SerializedName("store_long")
    @Expose
    var storeLong: String? = null
    @SerializedName("store_email")
    @Expose
    var storeEmail: String? = null
    @SerializedName("store_phone1")
    @Expose
    var storePhone1: String? = null
    @SerializedName("store_phone2")
    @Expose
    var storePhone2: String? = null
    @SerializedName("store_about")
    @Expose
    var storeAbout: String? = null
    @SerializedName("location_name")
    @Expose
    var locationName: String? = null
    @SerializedName("admin_type")
    @Expose
    var adminType: String? = null
    @SerializedName("city_id")
    @Expose
    var cityId: String? = null
    @SerializedName("city_name")
    @Expose
    var cityName: String? = null
    @SerializedName("state_id")
    @Expose
    var stateId: String? = null
    @SerializedName("state_name")
    @Expose
    var stateName: String? = null
    @SerializedName("currency_id")
    @Expose
    var currencyId: String? = null
    @SerializedName("currency_title")
    @Expose
    var currencyTitle: String? = null
    @SerializedName("store_countrycode")
    @Expose
    var storeCountrycode: String? = null
    @SerializedName("cust_app_orders")
    var clientKey: String = ""
    var machineId: String = ""

}

/*
class IndAppOrders {

    @SerializedName("tot_orders")
    @Expose
    var totOrders: String? = null
    @SerializedName("pending_orders")
    @Expose
    var pendingOrders: String? = null
    @SerializedName("accepted_orders")
    @Expose
    var acceptedOrders: String? = null
    @SerializedName("shipped_orders")
    @Expose
    var shippedOrders: String? = null

}


class CustAppOrders {

    @SerializedName("tot_orders")
    @Expose
    var totOrders: String? = null
    @SerializedName("pending_orders")
    @Expose
    var pendingOrders: String? = null
    @SerializedName("accepted_orders")
    @Expose
    var acceptedOrders: String? = null
    @SerializedName("shipped_orders")
    @Expose
    var shippedOrders: String? = null

}*/
