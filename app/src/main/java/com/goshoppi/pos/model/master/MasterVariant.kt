package com.goshoppi.pos.model.master

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


/**
 * Created by waqar.eid on 10/22/2016.
 */
@Entity(tableName = "master_variants")
class MasterVariant {
    @PrimaryKey
    @SerializedName("store_range_id")
    @Expose
    var storeRangeId: Long? = null
    /**
     *
     * @return
     * The sku
     */
    /**
     *
     * @param sku
     * The sku
     */
    @SerializedName("sku")
    @Expose
    var sku: String? = null
    /**
     *
     * @return
     * The productMrp
     */
    /**
     *
     * @param productMrp
     * The product_mrp
     */
    @SerializedName("product_mrp")
    @Expose
    var productMrp: String? = null//original price
    /**
     *
     * @return
     * The offerPrice
     */
    /**
     *
     * @param offerPrice
     * The offer_price
     */
    @SerializedName("offer_price")
    @Expose
    var offerPrice: String? = null//discounted price
    /**
     *
     * @return
     * The rangeName
     */
    /**
     *
     * @param rangeName
     * The range_name
     */
    @SerializedName("range_name")
    @Expose
    var rangeName: String? = null
    /**
     *
     * @return
     * The rangeId
     */
    /**
     *
     * @param rangeId
     * The range_id
     */
    @SerializedName("range_id")
    @Expose
    var rangeId: String? = null
    /**
     *
     * @return
     * The productImage
     */
    /**
     *
     * @param productImage
     * The product_image
     */
    @SerializedName("product_image")
    @Expose
    var productImage: String? = null
    /**
     *
     * @return
     * The unitId
     */
    /**
     *
     * @param unitId
     * The unit_id
     */
    @SerializedName("unit_id")
    @Expose
    var unitId: String? = null
    /**
     *
     * @return
     * The unitName
     */
    /**
     *
     * @param unitName
     * The unit_name
     */
    @SerializedName("unit_name")
    @Expose
    var unitName: String? = null
    /**
     *
     * @return
     * The barcode
     */
    /**
     *
     * The unit_name
     */
    @SerializedName("barcode")
    @Expose
    var barCode: String? = null

    /**
     *
     * @return
     * The purchaseLimit
     */
    /**
     *
     * @param purchaseLimit
     * The purchase_limit
     */
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


    var productId: Long? = null

    var discount: String? = null
    var productName: String = ""

}