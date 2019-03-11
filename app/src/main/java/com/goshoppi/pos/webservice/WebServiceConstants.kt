package com.goshoppi.pos.webservice


import com.goshoppi.pos.utilities.Constants

/**
 * Created by waqar.eid on 10/18/2016.
 */

object WebServiceConstants {
    var MERCHANT_QA_URL = "http://www.maridukan.com/merchantservices/"
    var MERCHANT_PR_URL = "http://www.maridukan.com/merchantservices/"


    var MERCHANT_QA_WEB_SERVICE_KEY = "goshoppi777"
    var MERCHANT_PR_WEB_SERVICE_KEY = "goshoppi777"

    var merchantWebServiceKey: String
        get() = if (Constants.isDebug)
            MERCHANT_QA_WEB_SERVICE_KEY
        else
            MERCHANT_PR_WEB_SERVICE_KEY
        set(key) = if (Constants.isDebug)
            MERCHANT_QA_WEB_SERVICE_KEY = key
        else
            MERCHANT_PR_WEB_SERVICE_KEY = key


}
