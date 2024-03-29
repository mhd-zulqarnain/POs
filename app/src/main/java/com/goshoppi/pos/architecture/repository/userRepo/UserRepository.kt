package com.goshoppi.pos.architecture.repository.userRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.model.AdminData
import com.goshoppi.pos.model.User

interface UserRepository {
    fun loadAllUser(): LiveData<List<User>>
    suspend fun loadLocalAllStaticUsers(): List<User>
    suspend fun insertUser(user: User):Long
    suspend fun insertAdminData(adminData: AdminData)
    suspend fun updateUser(isAdmin: Boolean, isProc: Boolean,isSales: Boolean,userId:Long)
    suspend fun searchLocalStaticUser(param: String): List<User>
    suspend fun updateMachineId(machineId:String)
    fun getMachineId():LiveData<String>
    fun getAdminData(): LiveData<AdminData>
    fun insertUsers(userList:List<User> )
    fun searchUsers(param: String): LiveData<List<User>>
    fun deleteUsers(userId: Int)
    fun getAuthResult(usercode: String,password: String): LiveData<List<User>>
    fun getSalesAuthResult(storeCode: String,userCode: String,password: String): LiveData<User>
    fun getProcAuthResult(storeCode: String,userCode: String,password: String): LiveData<User>
}