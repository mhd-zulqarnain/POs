package com.goshoppi.pos.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



@Entity(tableName = "products")
public class Product
{
    @SerializedName("category_id")
    @Expose
    private String categoryId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("subcategory_id")
    @Expose
    private String subcategoryId;
    @SerializedName("subcategory_name")
    @Expose
    private String subcategoryName;
    @PrimaryKey
    @NotNull
    @SerializedName("store_product_id")
    @Expose
    private String storeProductId;
    @SerializedName("product_images")
    @Expose
    private List<String> productImages;
    @SerializedName("product_tags")
    @Expose
    private List<String> productTags;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("small_description")
    @Expose
    private String smallDescription;
    @SerializedName("product_mrp")
    @Expose
    private String productMrp;
    @SerializedName("offer_price")
    @Expose
    private String offerPrice;
    @SerializedName("store_range_id")
    @Expose
    private String storeRangeId;
    @SerializedName("currency_id")
    @Expose
    private String currencyId;
    @SerializedName("currency_title")
    @Expose
    private String currencyTitle;
    @SerializedName("range_name")
    @Expose
    private String rangeName;
    @SerializedName("unit_name")
    @Expose
    private String unitName;
    @SerializedName("unlimited_stock")
    @Expose
    private String unlimitedStock;
    @SerializedName("out_of_stock")
    @Expose
    private String outOfStock;
    @SerializedName("purchase_limit")
    @Expose
    private String purchaseLimit;
    @SerializedName("stock_balance")
    @Expose
    private String stockBalance;
    @SerializedName("variants")
    @Expose
    private List<Variant> variants = new ArrayList<Variant>();

    private boolean isEdit = false;


    private String productImagesArray;

    public Product(){

    }

    public Product(String categoryId, String categoryName, String subcategoryId, String subcategoryName, String storeProductId, List<String> productImages, List<String> productTags, String productName, String smallDescription, String productMrp, String offerPrice, String storeRangeId, String currencyId, String currencyTitle, String rangeName, String unitName, String unlimitedStock, String outOfStock, String purchaseLimit, String stockBalance, List<Variant> variants, boolean isEdit, String productImagesArray) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.subcategoryId = subcategoryId;
        this.subcategoryName = subcategoryName;
        this.storeProductId = storeProductId;
        this.productImages = productImages;
        this.productTags = productTags;
        this.productName = productName;
        this.smallDescription = smallDescription;
        this.productMrp = productMrp;
        this.offerPrice = offerPrice;
        this.storeRangeId = storeRangeId;
        this.currencyId = currencyId;
        this.currencyTitle = currencyTitle;
        this.rangeName = rangeName;
        this.unitName = unitName;
        this.unlimitedStock = unlimitedStock;
        this.outOfStock = outOfStock;
        this.purchaseLimit = purchaseLimit;
        this.stockBalance = stockBalance;
        this.variants = variants;
        this.isEdit = isEdit;
        this.productImagesArray = productImagesArray;
    }

    /**
     *
     * @return
     * The categoryId
     */
    public String getCategoryId()
    {
        return categoryId;
    }

    /**
     *
     * @param categoryId
     * The category_id
     */
    public void setCategoryId(String categoryId)
    {
        this.categoryId = categoryId;
    }

    /**
     *
     * @return
     * The categoryName
     */
    public String getCategoryName()
    {
        return categoryName;
    }

    /**
     *
     * @param categoryName
     * The category_name
     */
    public void setCategoryName(String categoryName)
    {
        this.categoryName = categoryName;
    }

    /**
     *
     * @return
     * The subcategoryId
     */
    public String getSubcategoryId()
    {
        return subcategoryId;
    }

    /**
     *
     * @param subcategoryId
     * The subcategory_id
     */
    public void setSubcategoryId(String subcategoryId)
    {
        this.subcategoryId = subcategoryId;
    }

    /**
     *
     * @return
     * The subcategoryName
     */
    public String getSubcategoryName()
    {
        return subcategoryName;
    }

    /**
     *
     * @param subcategoryName
     * The subcategory_name
     */
    public void setSubcategoryName(String subcategoryName)
    {
        this.subcategoryName = subcategoryName;
    }

    /**
     *
     * @return
     * The storeProductId
     */
    public String getStoreProductId()
    {
        return storeProductId;
    }

    /**
     *
     * @param storeProductId
     * The store_product_id
     */
    public void setStoreProductId(String storeProductId)
    {
        this.storeProductId = storeProductId;
    }

    /**
     *
     * @return
     * The productImages
     */
    public List<String> getProductImages()
    {
        return productImages;
    }

    /**
     *
     * @param productImages
     * The product_images
     */
    public void setProductImages(List<String> productImages)
    {
        this.productImages = productImages;
    }

    /**
     *
     * @return
     * The productTags
     */
    public List<String> getProductTags()
    {
        return productTags;
    }

    /**
     *
     * @param productTags
     * The product_tags
     */
    public void setProductTags(List<String> productTags)
    {
        this.productTags = productTags;
    }

    /**
     *
     * @return
     * The productName
     */
    public String getProductName()
    {
        return productName;
    }

    /**
     *
     * @param productName
     * The product_name
     */
    public void setProductName(String productName)
    {
        this.productName = productName;
    }

    /**
     *
     * @return
     * The smallDescription
     */
    public String getSmallDescription()
    {
        return smallDescription;
    }

    /**
     *
     * @param smallDescription
     * The small_description
     */
    public void setSmallDescription(String smallDescription)
    {
        this.smallDescription = smallDescription;
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
     * The storeRangeId
     */
    public String getStoreRangeId()
    {
        return storeRangeId;
    }

    /**
     *
     * @param storeRangeId
     * The store_range_id
     */
    public void setStoreRangeId(String storeRangeId)
    {
        this.storeRangeId = storeRangeId;
    }

    /**
     *
     * @return
     * The currencyId
     */
    public String getCurrencyId()
    {
        return currencyId;
    }

    /**
     *
     * @param currencyId
     * The currency_id
     */
    public void setCurrencyId(String currencyId)
    {
        this.currencyId = currencyId;
    }

    /**
     *
     * @return
     * The currencyTitle
     */
    public String getCurrencyTitle()
    {
        return currencyTitle;
    }

    /**
     *
     * @param currencyTitle
     * The currency_title
     */
    public void setCurrencyTitle(String currencyTitle)
    {
        this.currencyTitle = currencyTitle;
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
     * The variants
     */
    public List<Variant> getVariants()
    {
        return variants;
    }

    /**
     *
     * @param variants
     * The variants
     */
    public void setVariants(List<Variant> variants)
    {
        this.variants = variants;
    }

    public boolean isEdit() {
        return isEdit;
    }

    public void setEdit(boolean edit) {
        isEdit = edit;
    }

    public String getProductImagesArray() {
        return productImagesArray;
    }

    public void setProductImagesArray(String productImagesArray) {
        this.productImagesArray = productImagesArray;
    }

    @Override
    public String toString() {
        return "Product{" +
                "categoryId='" + categoryId + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", subcategoryId='" + subcategoryId + '\'' +
                ", subcategoryName='" + subcategoryName + '\'' +
                ", storeProductId='" + storeProductId + '\'' +
                ", productImages=" + productImages +
                ", productTags=" + productTags +
                ", productName='" + productName + '\'' +
                ", smallDescription='" + smallDescription + '\'' +
                ", productMrp='" + productMrp + '\'' +
                ", offerPrice='" + offerPrice + '\'' +
                ", storeRangeId='" + storeRangeId + '\'' +
                ", currencyId='" + currencyId + '\'' +
                ", currencyTitle='" + currencyTitle + '\'' +
                ", rangeName='" + rangeName + '\'' +
                ", unitName='" + unitName + '\'' +
                ", unlimitedStock='" + unlimitedStock + '\'' +
                ", outOfStock='" + outOfStock + '\'' +
                ", purchaseLimit='" + purchaseLimit + '\'' +
                ", stockBalance='" + stockBalance + '\'' +
                ", variants=" + variants +
                ", isEdit=" + isEdit +
                ", productImagesArray='" + productImagesArray + '\'' +
                '}';
    }
}