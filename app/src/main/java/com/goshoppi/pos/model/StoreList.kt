package com.goshoppi.pos.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class StoreList {

    @SerializedName("store_id")
    @Expose
    var storeId: String? = null
    @SerializedName("store_name")
    @Expose
    var storeName: String? = null
    @SerializedName("business_type_id")
    @Expose
    var businessTypeId: String? = null
    @SerializedName("business_type")
    @Expose
    var businessType: String? = null
    @SerializedName("view_type")
    @Expose
    var viewType: String? = null
    @SerializedName("business_type_image")
    @Expose
    var businessTypeImage: String? = null
    @SerializedName("favorite_store")
    @Expose
    var favoriteStore: Int? = null
    @SerializedName("store_logo")
    @Expose
    var storeLogo: String? = null
    @SerializedName("store_shopiurl")
    @Expose
    var storeShopiurl: String? = null
    @SerializedName("store_web")
    @Expose
    var storeWeb: String? = null
    @SerializedName("store_add1")
    @Expose
    var storeAdd1: String? = null
    @SerializedName("store_add2")
    @Expose
    var storeAdd2: String? = null
    @SerializedName("store_location")
    @Expose
    var storeLocation: String? = null
    @SerializedName("store_pincode")
    @Expose
    var storePincode: String? = null
    @SerializedName("store_city")
    @Expose
    var storeCity: String? = null
    @SerializedName("store_country")
    @Expose
    var storeCountry: String? = null
    @SerializedName("store_countrycode")
    @Expose
    var storeCountrycode: String? = null
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
    @SerializedName("currency_title")
    @Expose
    var currencyTitle: String? = null
    @SerializedName("min_order_amt")
    @Expose
    var minOrderAmt: String? = null
    @SerializedName("min_del_duration")
    @Expose
    var minDelDuration: String? = null
    @SerializedName("imm_delivery_duration")
    @Expose
    var immDeliveryDuration: String? = null
    @SerializedName("imm_duration_unit")
    @Expose
    var immDurationUnit: String? = null
    @SerializedName("default_delivery_charge")
    @Expose
    var defaultDeliveryCharge: String? = null
    @SerializedName("min_delivery_charge")
    @Expose
    var minDeliveryCharge: Any? = null
    @SerializedName("min_delivery_text")
    @Expose
    var minDeliveryText: Any? = null
    @SerializedName("service_charge")
    @Expose
    var serviceCharge: String? = null
    @SerializedName("pickpack_duration")
    @Expose
    var pickpackDuration: String? = null
    @SerializedName("pickpack_duration_unit")
    @Expose
    var pickpackDurationUnit: String? = null
    @SerializedName("pickpack_max_days")
    @Expose
    var pickpackMaxDays: String? = null
    @SerializedName("store_alwaysopen")
    @Expose
    var storeAlwaysopen: String? = null
    @SerializedName("store_timings")
    @Expose
    var storeTimings: String? = null
    @SerializedName("store_banners")
    @Expose
    var storeBanners: List<StoreBanner>? = null
    @SerializedName("store_workdays")
    @Expose
    var storeWorkdays: StoreWorkdays? = null
    @SerializedName("store_payoptions")
    @Expose
    var storePayoptions: List<StorePayoption>? = null
    @SerializedName("map_delivery_regions")
    @Expose
    var mapDeliveryRegions: List<MapDeliveryRegions>? = null
    @SerializedName("location_delivery_regions")
    @Expose
    var locationDeliveryRegions: List<LocationDeliveryRegion>? = null
    @SerializedName("order_note")
    @Expose
    var orderNote: String? = null

}
