package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(
    tableName = "users"
)
class User {
    @PrimaryKey(autoGenerate = true)
    var userId: Long? = null
    var storeCode: String? = null
    var userCode: String ? = null
    var password: String ? = null
    var updatedAt :String ? = null
    var isAdmin :Boolean  = false
    var isProcurement :Boolean  = false
    var isSales :Boolean  = false
    var isSynced :Boolean  = false


}