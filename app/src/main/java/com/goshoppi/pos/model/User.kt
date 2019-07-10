package com.goshoppi.pos.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "users",
  indices = arrayOf(Index(value = ["userCode"], unique = true)))

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