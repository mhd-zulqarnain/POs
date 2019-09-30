package com.goshoppi.pos.model.master

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.goshoppi.pos.model.local.LocalVariant
import com.goshoppi.pos.utils.Constants

import java.util.ArrayList

@Entity(tableName = "master_products")
class MasterProduct {

    @ColumnInfo(name = "product_id")
    @NonNull
    @PrimaryKey
    @SerializedName("store_product_id")
    @Expose
    var storeProductId: Long = 0
    @SerializedName("category_id")
    @Expose
    var categoryId: Long? = null
    @SerializedName("category_name")
    @Expose
    var categoryName: String? = null
    @SerializedName("subcategory_id")
    @Expose
    var subcategoryId: Long? = null
    @SerializedName("subcategory_name")
    @Expose
    var subcategoryName: String? = null
    @SerializedName("product_images")
    @Expose
    var productImages: List<String>? = null
    @SerializedName("product_tags")
    @Expose
    var productTags: List<String>? = null
    @SerializedName("product_name")
    @Expose
    var productName: String? = null
    @SerializedName("small_description")
    @Expose
    var smallDescription: String? = null
    @SerializedName("product_mrp")
    @Expose
    var productMrp: String? = null
    @SerializedName("offer_price")
    @Expose
    var offerPrice: String? = null
    @SerializedName("store_range_id")
    @Expose
    var storeRangeId: String? = null
    @SerializedName("currency_id")
    @Expose
    var currencyId: String? = null
    @SerializedName("currency_title")
    @Expose
    var currencyTitle: String? = null
    @SerializedName("range_name")
    @Expose
    var rangeName: String? = null
    @SerializedName("barcode")
    @Expose
    var barcode: String? = null
    @SerializedName("unit_name")
    @Expose
    var unitName: String? = null
    @SerializedName("unlimited_stock")
    @Expose
    var unlimitedStock: String? = null
    @SerializedName("out_of_stock")
    @Expose
    var outOfStock: String? = null
    @SerializedName("purchase_limit")
    @Expose
    var purchaseLimit: String? = null
    @SerializedName("stock_balance")
    @Expose
    var stockBalance: String? = null
    @SerializedName("variants")
    @Expose
    @Ignore
    var variants: List<MasterVariant> = ArrayList()
    var isEdit = false
    var productImagesArray: String? = null
    var type = Constants.BAR_CODED_PRODUCT

}