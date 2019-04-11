package com.goshoppi.pos.architecture.repository.userRepo

import android.arch.lifecycle.LiveData
import com.goshoppi.pos.architecture.dao.UserDao
import com.goshoppi.pos.model.User
import javax.inject.Inject

class UserRepositoryImp @Inject constructor(var userDao: UserDao):UserRepository{
    override fun getSalesAuthResult(storeCode: String, userCode: String, password: String): LiveData<User> {
    return  userDao.getSalesAuthResult(storeCode,userCode,password)
    }

    override fun getProcAuthResult(storeCode: String, userCode: String, password: String): LiveData<User> {
        return  userDao.getProcAuthResult(storeCode,userCode,password)
    }

    override fun getAuthResult(usercode: String, password: String): LiveData<List<User>> {
        return  userDao.getAuthResult(usercode,password)
    }

    override fun loadAllUser(): LiveData<List<User>> {
       return userDao.loadLocalAllUsers()
    }

    override fun insertUser(user: User) {
   userDao.insertUser(user = user)
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