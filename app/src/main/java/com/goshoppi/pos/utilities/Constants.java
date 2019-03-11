package com.goshoppi.pos.utilities;



import com.goshoppi.pos.model.Product;
import com.goshoppi.pos.model.StoreList;

import java.util.HashMap;

/**
 * Created by waqar.eid on 10/18/2016.
 */

public class Constants {
    public static boolean isDebug = true;
     static String LOG_TAG = "MerchantLogs";

    /*cart items*/
    public static String promocode = "";
    public static HashMap<String, Product> cartData = new HashMap<>();
    public static Product selectedProduct;
    public static double totalCartAmount = 0.00;
     static Double requiredMinAmount = 0d;
     static Double orderAmountDeliveryCharges =0d;
    public static StoreList selectedStoreList;

    public static Double cartTotalDiscount =0d;



}
