package com.goshoppi.pos.model

import com.goshoppi.pos.model.local.LocalCustomer

class HoldOrder(
    var holdcustomer: LocalCustomer? = null,
    var holdorderlist: ArrayList<OrderItem>? = null,
    var holdorderId: Long? = null,
    var holdorderTotal: Double? = null

)