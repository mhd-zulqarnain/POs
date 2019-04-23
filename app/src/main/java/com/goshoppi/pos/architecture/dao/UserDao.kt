package com.goshoppi.pos.architecture.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.goshoppi.pos.model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM users")
    fun loadLocalAllUsers(): LiveData<List<User>>

    @Query("SELECT * FROM users")
    fun loadLocalAllStaticUsers(): List<User>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: User)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<User>)

    @Query("DELETE FROM  users WHERE  userId= :userId")
    fun deleteUser(userId: Int)

    @Query("SELECT * FROM  users WHERE userCode LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<User>>

    @Query("SELECT * FROM  users WHERE userCode= :usercode  AND password= :password AND isAdmin=1")
    fun getAuthResult(usercode: String, password: String): LiveData<List<User>>

    @Query("SELECT * FROM  users WHERE userCode= :userCode  AND password= :password AND isSales=1 AND storeCode=:storeCode")
    fun getSalesAuthResult(storeCode: String,userCode: String,password: String): LiveData<User>

    @Query("SELECT * FROM  users WHERE userCode= :userCode  AND password= :password AND isProcurement=1 AND storeCode=:storeCode")
    fun getProcAuthResult(storeCode: String,userCode: String,password: String): LiveData<User>

}
