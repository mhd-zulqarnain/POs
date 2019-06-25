package com.goshoppi.pos.model.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.annotation.NonNull
import com.google.gson.annotations.Expose


@Entity(
    tableName = "local_customers"
)
class LocalCustomer {
    @ColumnInfo(name = "phone")
    @NonNull
    @PrimaryKey
    @Expose
    var phone: Long = 0
    @Expose
    var alternativePhone: String? = null
    @Expose
    var name: String ? = null
    @Expose
    var address: String ? = null
    @Expose
    var gstin :String ? = null
    @Expose
    var updatedAt :String ? = null
    var isSynced :Boolean ? = null
    @Expose
    var totalCredit :Double  = 0.00

}