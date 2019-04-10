package com.goshoppi.pos.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.support.annotation.NonNull


@Entity(
    tableName = "users"
)
class User {
    @PrimaryKey(autoGenerate = true)
    var userId: Int = 0
    var storeCode: String? = null
    var userCode: String ? = null
    var password: String ? = null
    var updatedAt :String ? = null
    var isAdmin :Boolean  = false
    var isProcurement :Boolean  = false
    var isSales :Boolean  = false
    var isSynced :Boolean  = false


}