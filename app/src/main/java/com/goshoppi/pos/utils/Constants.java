package com.goshoppi.pos.utils;


public class Constants {
    public static boolean isDebug = true;
    public static boolean workerInitialization = false;

    public static final String BASE_URL = "http://www.maridukan.com/merchantservices/";
    public static final String PRODUCT_OBJECT_KEY = "productObjectKey";
    public static final String PRODUCT_OBJECT_INTENT = "productObjectintent";
    public static final String MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY = "MAIN_WORKER_FETCH_MASTER_TO_TERMINAL_ONLY_ONCE_KEY";
    public static final String CHANNEL_ID = "MasterDataBaseSyncChannelId";
    public static final int NOTIFY_ID = 123;
    public static final String STORE_VARIANT_IMAGE_WORKER_TAG = "storeVariantImageWorkerTAG";
    public static final String DATABASE_NAME = "GoShoppiPosDb";
    public static final String ONE_TIME_WORK = "forOnce";
    public static String DEVELOPER_KEY = "goshoppi777";
    /*
     * Image of product would be save in prd_{ id of product}
     * Image of variant would be save in prd_{ id of product}/variant_images/
     * */
    public static String PRODUCT_IMAGE_DIR = "prd_";
    public static String VARIANT_IMAGE_DIR = "variant_images";


}
