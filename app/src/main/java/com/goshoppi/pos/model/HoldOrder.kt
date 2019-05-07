package com.goshoppi.pos.model

import com.goshoppi.pos.model.local.LocalCustomer
import com.goshoppi.pos.model.local.LocalVariant

class HoldOrder(
    var holdcustomer: LocalCustomer? = null,
    var holdorderlist: ArrayList<OrderItem>? = null,
    var varaintList: ArrayList<LocalVariant>? = null,
    var holdorderId: Long? = null,
    var holdorderTotal: Double? = null

)