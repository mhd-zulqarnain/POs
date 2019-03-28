package com.goshoppi.pos.model.local

import android.arch.persistence.room.*
import android.support.annotation.NonNull
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.ArrayList


@Entity(
    tableName = "local_products"
)
class LocalProduct {

    @ColumnInfo(name = "product_id")
    @NonNull
    @PrimaryKey
    @SerializedName("store_product_id")
    @Expose
    var storeProductId: Int = 0

    @SerializedName("category_id")
    @Expose
    var categoryId: String? = null

    @SerializedName("category_name")
    @Expose
    var categoryName: String? = null
    @SerializedName("subcategory_id")
    @Expose
    var subcategoryId: String? = null
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
    var productMrp: String? = null
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
    var offerPrice: String? = null
    /**
     *
     * @return
     * The storeRangeId
     */
    /**
     *
     * @param storeRangeId
     * The store_range_id
     */
    @SerializedName("store_range_id")
    @Expose
    var storeRangeId: String? = null
    /**
     *
     * @return
     * The currencyId
     */
    /**
     *
     * @param currencyId
     * The currency_id
     */
    @SerializedName("currency_id")
    @Expose
    var currencyId: String? = null
    /**
     *
     * @return
     * The currencyTitle
     */
    /**
     *
     * @param currencyTitle
     * The currency_title
     */
    @SerializedName("currency_title")
    @Expose
    var currencyTitle: String? = null
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
    /**
     *
     * @return
     * The variants
     */
    /**
     *
     * @param variants
     * The variants
     */
    @SerializedName("variants")
    @Expose
    @Ignore
    var variants: List<LocalVariant> = ArrayList()

    var isEdit = false


    var productImagesArray: String? = null

    constructor() {

    }

    constructor(
        categoryId: String,
        categoryName: String,
        subcategoryId: String,
        subcategoryName: String,
        storeProductId: Int,
        productImages: List<String>,
        productTags: List<String>,
        productName: String,
        smallDescription: String,
        productMrp: String,
        offerPrice: String,
        storeRangeId: String,
        currencyId: String,
        currencyTitle: String,
        rangeName: String,
        unitName: String,
        unlimitedStock: String,
        outOfStock: String,
        purchaseLimit: String,
        stockBalance: String,
        variants: List<LocalVariant>,
        isEdit: Boolean,
        productImagesArray: String
    ) {
        this.categoryId = categoryId
        this.categoryName = categoryName
        this.subcategoryId = subcategoryId
        this.subcategoryName = subcategoryName
        this.storeProductId = storeProductId
        this.productImages = productImages
        this.productTags = productTags
        this.productName = productName
        this.smallDescription = smallDescription
        this.productMrp = productMrp
        this.offerPrice = offerPrice
        this.storeRangeId = storeRangeId
        this.currencyId = currencyId
        this.currencyTitle = currencyTitle
        this.rangeName = rangeName
        this.unitName = unitName
        this.unlimitedStock = unlimitedStock
        this.outOfStock = outOfStock
        this.purchaseLimit = purchaseLimit
        this.stockBalance = stockBalance
        this.variants = variants
        this.isEdit = isEdit
        this.productImagesArray = productImagesArray
    }

    override fun toString(): String {
        return "MasterProduct{" +
                "categoryId='" + categoryId + '\''.toString() +
                ", categoryName='" + categoryName + '\''.toString() +
                ", subcategoryId='" + subcategoryId + '\''.toString() +
                ", subcategoryName='" + subcategoryName + '\''.toString() +
                ", storeProductId='" + storeProductId + '\''.toString() +
                ", productImages=" + productImages +
                ", productTags=" + productTags +
                ", productName='" + productName + '\''.toString() +
                ", smallDescription='" + smallDescription + '\''.toString() +
                ", productMrp='" + productMrp + '\''.toString() +
                ", offerPrice='" + offerPrice + '\''.toString() +
                ", storeRangeId='" + storeRangeId + '\''.toString() +
                ", currencyId='" + currencyId + '\''.toString() +
                ", currencyTitle='" + currencyTitle + '\''.toString() +
                ", rangeName='" + rangeName + '\''.toString() +
                ", unitName='" + unitName + '\''.toString() +
                ", unlimitedStock='" + unlimitedStock + '\''.toString() +
                ", outOfStock='" + outOfStock + '\''.toString() +
                ", purchaseLimit='" + purchaseLimit + '\''.toString() +
                ", stockBalance='" + stockBalance + '\''.toString() +
                ", variants=" + variants +
                ", isEdit=" + isEdit +
                ", productImagesArray='" + productImagesArray + '\''.toString() +
                '}'.toString()
    }
}