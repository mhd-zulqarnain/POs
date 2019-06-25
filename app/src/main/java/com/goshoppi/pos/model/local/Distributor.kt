package com.goshoppi.pos.model.local

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "distributors"
)
class Distributor{
    @ColumnInfo(name = "phone")
    @NonNull
    @PrimaryKey
    var phone: Long = 0
    var alternativePhone: String? = null
    var name: String ? = null
    var address: String ? = null
    var gstin :String ? = null
    var updatedAt :String ? = null
  
    var isSynced :Boolean ? = null
    var totalCredit :Double  = 0.00
}