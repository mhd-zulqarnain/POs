package com.goshoppi.pos.model.local

import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import androidx.annotation.NonNull
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.goshoppi.pos.utils.Constants


/**
 * Created by waqar.eid on 10/22/2016.
 */

@Entity(
    tableName = "local_variants"
  /*  foreignKeys = [ForeignKey(
        entity = LocalProduct::class,
        parentColumns = ["product_id"],
        childColumns = ["product_id"])
    ]*/
//        onDelete = CASCADE)]

/*
* Casacade removed
* Reason :on updating product from csv the respective get removed
* */
)
class LocalVariant {
    @NonNull
    @PrimaryKey
    @SerializedName("store_range_id")
    @Expose
    var storeRangeId: Long = 0

    @SerializedName("sku")
    @Expose
    var sku: String? = null

    @SerializedName("product_mrp")
    @Expose
    var productMrp: String? = null//original price

    @SerializedName("offer_price")
    @Expose
    var offerPrice: String? = null//discounted price

    @SerializedName("range_name")
    @Expose
    var rangeName: String? = null

    @SerializedName("range_id")
    @Expose
    var rangeId: String? = null

    @SerializedName("product_image")
    @Expose
    var productImage: String? = null

    @SerializedName("unit_id")
    @Expose
    var unitId: String? = null

    @SerializedName("unit_name")
    @Expose
    var unitName: String? = null

    @SerializedName("barcode")
    @Expose
    var barCode: String? = null

    @SerializedName("purchase_limit")
    @Expose
    var purchaseLimit: String? = null
    /**
     *
     * @return
     * The unlimitedStock
     */
    /**
     *
     * @param unlimitedStock
     * The unlimited_stock
     */
    @SerializedName("unlimited_stock")
    @Expose
    var unlimitedStock: String? = null
    /**
     *
     * @return
     * The stockBalance
     */
    /**
     *
     * @param stockBalance
     * The stock_balance
     */
    @SerializedName("stock_balance")
    @Expose
    var stockBalance: String? = null
    /**
     *
     * @return
     * The outOfStock
     */
    /**
     *
     * @param outOfStock
     * The out_of_stock
     */
    @SerializedName("out_of_stock")
    @Expose
    var outOfStock: String? = null
    /**
     *
     * @return
     * The offer_product
     */
    /**
     *
     * @param offer_product
     * The out_of_stock
     */
    @SerializedName("offer_product")
    @Expose
    var offer_product: String? = null

    @ColumnInfo(name = "product_id", index = true)
    var productId: Long = 0

    var discount: String? = null
    var productName: String = ""
    var type = Constants.BAR_CODED_PRODUCT

}