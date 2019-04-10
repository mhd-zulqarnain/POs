package com.goshoppi.pos.architecture.dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
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
    fun insertUsers(users:  List<User>)

    @Query("DELETE FROM  users WHERE  userId= :userId")
    fun deleteUser(userId: Int)

    @Query("SELECT * FROM  users WHERE userCode LIKE '%' || :dealText || '%'")
    fun getLocalSearchResult(dealText: String): LiveData<List<User>>
}
