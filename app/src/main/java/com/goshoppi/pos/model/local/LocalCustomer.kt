package com.goshoppi.pos.model.local

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity(
    tableName = "local_customers"
)
class LocalCustomer {
    @ColumnInfo(name = "phone")
    @NonNull
    @PrimaryKey
    var phone: Int = 0
    var alternativePhone: Int? = null
    var name: String ? = null
    var address: String ? = null
    var gstin :String ? = null


}