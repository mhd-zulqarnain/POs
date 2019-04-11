package com.goshoppi.pos.architecture.repository.userRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.model.User

interface UserRepository {
    fun loadAllUser(): LiveData<List<User>>
    fun insertUser(user: User)
    fun insertUsers(userList:List<User> )
    fun searchUsers(param: String): LiveData<List<User>>
    fun deleteUsers(userId: Int)
    fun getAuthResult(usercode: String,password: String): LiveData<List<User>>
    fun getSalesAuthResult(storeCode: String,userCode: String,password: String): LiveData<User>
    fun getProcAuthResult(storeCode: String,userCode: String,password: String): LiveData<User>
}