package com.goshoppi.pos.architecture.repository.userRepo

import androidx.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.UserDao
import com.goshoppi.pos.di2.scope.AppScoped
import com.goshoppi.pos.model.AdminData
import com.goshoppi.pos.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AppScoped
class UserRepositoryImp @Inject constructor(var userDao: UserDao) : UserRepository {
    override suspend fun searchLocalStaticUser(param: String): List<User> {
        return withContext(Dispatchers.IO) {
            userDao.searchLocalStaticUser(param)
        }
    }

    override suspend fun loadLocalAllStaticUsers(): List<User> {
        return withContext(Dispatchers.IO) { userDao.loadLocalAllStaticUsers() }
    }

    override suspend fun updateMachineId(machineId: String) {
        withContext(Dispatchers.IO) { userDao.updateMachineId(machineId) }
    }

    override fun getMachineId(): LiveData<String> {
        return userDao.getMachineId()
    }

    override suspend fun updateUser(isAdmin: Boolean, isProc: Boolean, isSales: Boolean, userId: Long) {
        withContext(Dispatchers.IO) { userDao.updateUser(isAdmin, isProc, isSales, userId) }
    }

    override suspend fun insertAdminData(adminData: AdminData) {
        withContext(Dispatchers.IO) {
            userDao.insertAdminData(adminData)
        }
    }

    override fun getAdminData(): LiveData<AdminData> {
        return userDao.getAdminData()
    }

    override fun getSalesAuthResult(storeCode: String, userCode: String, password: String): LiveData<User> {
        return userDao.getSalesAuthResult(storeCode, userCode, password)
    }

    override fun getProcAuthResult(storeCode: String, userCode: String, password: String): LiveData<User> {
        return userDao.getProcAuthResult(storeCode, userCode, password)
    }

    override fun getAuthResult(usercode: String, password: String): LiveData<List<User>> {
        return userDao.getAuthResult(usercode, password)
    }

    override fun loadAllUser(): LiveData<List<User>> {
        return userDao.loadLocalAllUsers()
    }

    override suspend fun insertUser(user: User): Long {
        return withContext(Dispatchers.IO) { userDao.insertUser(user = user) }
    }

    override fun insertUsers(userList: List<User>) {
        userDao.insertUsers(userList)
    }

    override fun searchUsers(param: String): LiveData<List<User>> {
        return userDao.getLocalSearchResult(param)
    }

    override fun deleteUsers(userId: Int) {
        userDao.deleteUser(userId)
    }
}