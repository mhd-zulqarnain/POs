package com.goshoppi.pos.utils;


import com.goshoppi.pos.model.Product;
import com.goshoppi.pos.model.StoreList;

import java.util.HashMap;

public class Constants {
    static boolean isDebug = true;
    static String LOG_TAG = "MerchantLogs";

    public static final String BASE_URL = "http://www.maridukan.com/merchantservices/";
    public static final String PRODUCT_OBJECT_KEY = "productObjectKey";
    public static final String PRODUCT_OBJECT_INTENT = "productObjectintent";
    public static final String MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY = "MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY";
    public static final String  CHANNEL_ID = "MasterDataBaseSyncChannelId";
    public static final int NOTIFY_ID = 123;
    public static final String STORE_VARIANT_IMAGE_WORKER_TAG = "storeVariantImageWorkerTAG";

    /*
    * Image of prdouct would be save in prd_{ id of prdouct}
    * Image of varaint would be save in prd_{ id of varaint}
    * */
    public static String PRODUCT_IMAGE_DIR = "prd_";
    public static String VARAINT_IMAGE_DIR = "varaint_images";




}
