package com.goshoppi.pos.utils;


import com.goshoppi.pos.model.HoldOrder;

import java.util.ArrayList;

public class Constants {
    public static boolean isDebug = true;
    public static boolean workerInitialization = false;

    public static final String BASE_URL = "http://www.maridukan.com/";
    public static final String PRODUCT_OBJECT_INTENT = "productObjectintent";
    public static final String MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY = "MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY";
    public static final String CHANNEL_ID = "MasterDataBaseSyncChannelId";
    public static final int    NOTIFY_ID = 123;
    public static final String STORE_VARIANT_IMAGE_WORKER_TAG = "storeVariantImageWorkerTAG";
    public static final String DATABASE_NAME = "GoShoppiPosDb";
    public static final String ONE_TIME_WORK = "forOnce";
    public static String DEVELOPER_KEY = "goshoppi777";
    public static int UPDATE_VARIANT = 1223;
    /*
     * Image of product would be save in prd_{ id of product}
     * Image of variant would be save in prd_{ id of product}/variant_images/
     * */
    public static String PRODUCT_IMAGE_DIR = "prd_";

    public static int WEIGHTED_PRODUCT = 1;
    public static int BAR_CODED_PRODUCT= 2;

    public static String VARIANT_IMAGE_DIR = "variant_images";
    public static String CVS_PRODUCT_FILE = "csvproduct.csv";
    public static String CVS_VARIANT_FILE = "csvvariant.csv";

    /*payemnet status*/
    public static String PAID = "paid";
    public static String CREDIT = "credit";
    public static String ANONYMOUS = "Anonymous";


    public static ArrayList<HoldOrder> HOLDED_ORDER_LIST =new ArrayList<HoldOrder>();
}
