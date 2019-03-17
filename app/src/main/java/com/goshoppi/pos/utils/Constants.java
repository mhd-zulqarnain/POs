package com.goshoppi.pos.utils;


import com.goshoppi.pos.model.Product;
import com.goshoppi.pos.model.StoreList;

import java.util.HashMap;

public class Constants {
    static boolean isDebug = true;
    static String LOG_TAG = "MerchantLogs";

    /*cart items*/
    public static String promocode = "";
    public static String KEY_CURRENT_THEME = "current_theme";
    public static String GREEN_THEME = "green_theme";
    public static String DEFAULT_APP_THEME = "default_theme";
    public static HashMap<String, Product> cartData = new HashMap<>();
    public static Product selectedProduct;
    public static double totalCartAmount = 0.00;
    static Double requiredMinAmount = 0d;
    static Double orderAmountDeliveryCharges = 0d;
    public static StoreList selectedStoreList;
    public static Double cartTotalDiscount = 0d;
    public static final String BASE_URL = "http://www.maridukan.com/merchantservices/";


}
