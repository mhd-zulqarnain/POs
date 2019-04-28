package com.goshoppi.pos.model.master;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;


/**
 * Created by waqar.eid on 10/22/2016.
 */
@Entity(tableName = "master_variants")
public class MasterVariant
{
    @PrimaryKey
    @NotNull
    @SerializedName("store_range_id")
    @Expose
    private int storeRangeId;
    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("product_mrp")
    @Expose
    private String productMrp;//original price
    @SerializedName("offer_price")
    @Expose
    private String offerPrice;//discounted price
    @SerializedName("range_name")
    @Expose
    private String rangeName;
    @SerializedName("range_id")
    @Expose
    private String rangeId;
    @SerializedName("product_image")
    @Expose
    private String productImage;
    @SerializedName("unit_id")
    @Expose
    private String unitId;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("barcode")
    @Expose
    private String barCode;

    public int getStoreRangeId() {
        return storeRangeId;
    }

    public void setStoreRangeId(int storeRangeId) {
        this.storeRangeId = storeRangeId;
    }

    @SerializedName("purchase_limit")
    @Expose
    private String purchaseLimit;
    @SerializedName("unlimited_stock")
    @Expose
    private String unlimitedStock;
    @SerializedName("stock_balance")
    @Expose
    private String stockBalance;
    @SerializedName("out_of_stock")
    @Expose
    private String outOfStock;
    @SerializedName("offer_product")
    @Expose
    private String offer_product;


    private int productId;

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    private String discount;






    /**
     *
     * @return
     * The sku
     */
    public String getSku()
    {
        return sku;
    }

    /**
     *
     * @param sku
     * The sku
     */
    public void setSku(String sku)
    {
        this.sku = sku;
    }




    /**
     *
     * @return
     * The productMrp
     */
    public String getProductMrp()
    {
        return productMrp;
    }

    /**
     *
     * @param productMrp
     * The product_mrp
     */
    public void setProductMrp(String productMrp)
    {
        this.productMrp = productMrp;
    }

    /**
     *
     * @return
     * The offerPrice
     */
    public String getOfferPrice()
    {
        return offerPrice;
    }

    /**
     *
     * @param offerPrice
     * The offer_price
     */
    public void setOfferPrice(String offerPrice)
    {
        this.offerPrice = offerPrice;
    }

    /**
     *
     * @return
     * The rangeName
     */
    public String getRangeName()
    {
        return rangeName;
    }

    /**
     *
     * @param rangeName
     * The range_name
     */
    public void setRangeName(String rangeName)
    {
        this.rangeName = rangeName;
    }

    /**
     *
     * @return
     * The rangeId
     */
    public String getRangeId()
    {
        return rangeId;
    }

    /**
     *
     * @param rangeId
     * The range_id
     */
    public void setRangeId(String rangeId)
    {
        this.rangeId = rangeId;
    }

    /**
     *
     * @return
     * The productImage
     */
    public String getProductImage()
    {
        return productImage;
    }

    /**
     *
     * @param productImage
     * The product_image
     */
    public void setProductImage(String productImage)
    {
        this.productImage = productImage;
    }

    /**
     *
     * @return
     * The unitId
     */
    public String getUnitId()
    {
        return unitId;
    }

    /**
     *
     * @param unitId
     * The unit_id
     */
    public void setUnitId(String unitId)
    {
        this.unitId = unitId;
    }

    /**
     *
     * @return
     * The unitName
     */
    public String getUnitName()
    {
        return unitName;
    }

    /**
     *
     * @param unitName
     * The unit_name
     */
    public void setUnitName(String unitName)
    {
        this.unitName = unitName;
    }

    /**
     *
     * @return
     * The barcode
     */
    public String getBarCode()
    {
        return barCode;
    }

    /**
     *
     * The unit_name
     */
    public void setBarCode(String barCode)
    {
        this.barCode = barCode;
    }

    /**
     *
     * @return
     * The purchaseLimit
     */
    public String getPurchaseLimit()
    {
        return purchaseLimit;
    }

    /**
     *
     * @param purchaseLimit
     * The purchase_limit
     */
    public void setPurchaseLimit(String purchaseLimit)
    {
        this.purchaseLimit = purchaseLimit;
    }

    /**
     *
     * @return
     * The unlimitedStock
     */
    public String getUnlimitedStock()
    {
        return unlimitedStock;
    }

    /**
     *
     * @param unlimitedStock
     * The unlimited_stock
     */
    public void setUnlimitedStock(String unlimitedStock)
    {
        this.unlimitedStock = unlimitedStock;
    }

    /**
     *
     * @return
     * The stockBalance
     */
    public String getStockBalance()
    {
        return stockBalance;
    }

    /**
     *
     * @param stockBalance
     * The stock_balance
     */
    public void setStockBalance(String stockBalance)
    {
        this.stockBalance = stockBalance;
    }

    /**
     *
     * @return
     * The outOfStock
     */
    public String getOutOfStock()
    {
        return outOfStock;
    }

    /**
     *
     * @param outOfStock
     * The out_of_stock
     */
    public void setOutOfStock(String outOfStock)
    {
        this.outOfStock = outOfStock;
    }



    /**
     *
     * @return
     * The offer_product
     */
    public String getOffer_product()
    {
        return offer_product;
    }

    /**
     *
     * @param offer_product
     * The out_of_stock
     */
    public void setOffer_product(String offer_product)
    {
        this.offer_product = offer_product;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }
}