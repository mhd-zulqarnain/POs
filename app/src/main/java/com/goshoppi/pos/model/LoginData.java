package com.goshoppi.pos.model;

/**
 * Created by waqar.eid on 10/19/2016.
 */
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("admin_id")
    @Expose
    private String adminId;
    @SerializedName("admin_name")
    @Expose
    private String adminName;
    @SerializedName("admin_email")
    @Expose
    private String adminEmail;
    @SerializedName("store_id")
    @Expose
    private String storeId;
    @SerializedName("store_name")
    @Expose
    private String storeName;
    @SerializedName("store_logo")
    @Expose
    private String storeLogo;
    @SerializedName("store_shopiurl")
    @Expose
    private String storeShopiurl;
    @SerializedName("business_type")
    @Expose
    private String businessType;
    @SerializedName("store_web")
    @Expose
    private String storeWeb;
    @SerializedName("store_add1")
    @Expose
    private String storeAdd1;
    @SerializedName("store_add2")
    @Expose
    private String storeAdd2;
    @SerializedName("store_lat")
    @Expose
    private String storeLat;
    @SerializedName("store_long")
    @Expose
    private String storeLong;
    @SerializedName("store_email")
    @Expose
    private String storeEmail;
    @SerializedName("store_phone1")
    @Expose
    private String storePhone1;
    @SerializedName("store_phone2")
    @Expose
    private String storePhone2;
    @SerializedName("store_about")
    @Expose
    private String storeAbout;
    @SerializedName("location_name")
    @Expose
    private String locationName;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("currency_id")
    @Expose
    private String currencyId;
    @SerializedName("currency_title")
    @Expose
    private String currencyTitle;
    @SerializedName("store_countrycode")
    @Expose
    private String storeCountrycode;
    private String clientKey;
    /**
     *
     * @return
     * The adminId
     */
    public String getAdminId() {
        return adminId;
    }

    /**
     *
     * @param adminId
     * The admin_id
     */
    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    /**
     *
     * @return
     * The adminName
     */
    public String getAdminName() {
        return adminName;
    }

    /**
     *
     * @param adminName
     * The admin_name
     */
    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    /**
     *
     * @return
     * The adminEmail
     */
    public String getAdminEmail() {
        return adminEmail;
    }

    /**
     *
     * @param adminEmail
     * The admin_email
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     *
     * @return
     * The storeId
     */
    public String getStoreId() {
        return storeId;
    }

    /**
     *
     * @param storeId
     * The store_id
     */
    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    /**
     *
     * @return
     * The storeName
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     *
     * @param storeName
     * The store_name
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     *
     * @return
     * The storeLogo
     */
    public String getStoreLogo() {
        return storeLogo;
    }

    /**
     *
     * @param storeLogo
     * The store_logo
     */
    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    /**
     *
     * @return
     * The storeShopiurl
     */
    public String getStoreShopiurl() {
        return storeShopiurl;
    }

    /**
     *
     * @param storeShopiurl
     * The store_shopiurl
     */
    public void setStoreShopiurl(String storeShopiurl) {
        this.storeShopiurl = storeShopiurl;
    }

    /**
     *
     * @return
     * The businessType
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     *
     * @param businessType
     * The business_type
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     *
     * @return
     * The storeWeb
     */
    public String getStoreWeb() {
        return storeWeb;
    }

    /**
     *
     * @param storeWeb
     * The store_web
     */
    public void setStoreWeb(String storeWeb) {
        this.storeWeb = storeWeb;
    }

    /**
     *
     * @return
     * The storeAdd1
     */
    public String getStoreAdd1() {
        return storeAdd1;
    }

    /**
     *
     * @param storeAdd1
     * The store_add1
     */
    public void setStoreAdd1(String storeAdd1) {
        this.storeAdd1 = storeAdd1;
    }

    /**
     *
     * @return
     * The storeAdd2
     */
    public String getStoreAdd2() {
        return storeAdd2;
    }

    /**
     *
     * @param storeAdd2
     * The store_add2
     */
    public void setStoreAdd2(String storeAdd2) {
        this.storeAdd2 = storeAdd2;
    }

    /**
     *
     * @return
     * The storeLat
     */
    public String getStoreLat() {
        return storeLat;
    }

    /**
     *
     * @param storeLat
     * The store_lat
     */
    public void setStoreLat(String storeLat) {
        this.storeLat = storeLat;
    }

    /**
     *
     * @return
     * The storeLong
     */
    public String getStoreLong() {
        return storeLong;
    }

    /**
     *
     * @param storeLong
     * The store_long
     */
    public void setStoreLong(String storeLong) {
        this.storeLong = storeLong;
    }

    /**
     *
     * @return
     * The storeEmail
     */
    public String getStoreEmail() {
        return storeEmail;
    }

    /**
     *
     * @param storeEmail
     * The store_email
     */
    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }

    /**
     *
     * @return
     * The storePhone1
     */
    public String getStorePhone1() {
        return storePhone1;
    }

    /**
     *
     * @param storePhone1
     * The store_phone1
     */
    public void setStorePhone1(String storePhone1) {
        this.storePhone1 = storePhone1;
    }

    /**
     *
     * @return
     * The storePhone2
     */
    public String getStorePhone2() {
        return storePhone2;
    }

    /**
     *
     * @param storePhone2
     * The store_phone2
     */
    public void setStorePhone2(String storePhone2) {
        this.storePhone2 = storePhone2;
    }

    /**
     *
     * @return
     * The storeAbout
     */
    public String getStoreAbout() {
        return storeAbout;
    }

    /**
     *
     * @param storeAbout
     * The store_about
     */
    public void setStoreAbout(String storeAbout) {
        this.storeAbout = storeAbout;
    }

    /**
     *
     * @return
     * The locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     *
     * @param locationName
     * The location_name
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     *
     * @return
     * The cityId
     */
    public String getCityId() {
        return cityId;
    }

    /**
     *
     * @param cityId
     * The city_id
     */
    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    /**
     *
     * @return
     * The cityName
     */
    public String getCityName() {
        return cityName;
    }

    /**
     *
     * @param cityName
     * The city_name
     */
    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    /**
     *
     * @return
     * The stateId
     */
    public String getStateId() {
        return stateId;
    }

    /**
     *
     * @param stateId
     * The state_id
     */
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    /**
     *
     * @return
     * The stateName
     */
    public String getStateName() {
        return stateName;
    }

    /**
     *
     * @param stateName
     * The state_name
     */
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    /**
     *
     * @return
     * The currencyId
     */
    public String getCurrencyId() {
        return currencyId;
    }

    /**
     *
     * @param currencyId
     * The currency_id
     */
    public void setCurrencyId(String currencyId) {
        this.currencyId = currencyId;
    }

    /**
     *
     * @return
     * The currencyTitle
     */
    public String getCurrencyTitle() {
        return currencyTitle;
    }

    /**
     *
     * @param currencyTitle
     * The currency_title
     */
    public void setCurrencyTitle(String currencyTitle) {
        this.currencyTitle = currencyTitle;
    }

    /**
     *
     * @return
     * The storeCountrycode
     */
    public String getStoreCountrycode() {
        return storeCountrycode;
    }

    /**
     *
     * @param storeCountrycode
     * The store_countrycode
     */
    public void setStoreCountrycode(String storeCountrycode) {
        this.storeCountrycode = storeCountrycode;
    }

    /**
     *
     * @return
     * The clientKey
     */
    public String getClientKey() {
        return clientKey;
    }

    /**
     *
     * @param clientKey
     * The clientKey
     */
    public void setClientKey(String clientKey) {
        this.clientKey = clientKey;
    }

}