package com.magda.mamasbiz.main.data.repository

import androidx.lifecycle.LiveData
import com.magda.mamasbiz.main.data.dao.CreditDebtDao
import com.magda.mamasbiz.main.data.dao.UserDao
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.User

class UserRepository( private val userDao: UserDao){

    suspend fun addUser (user: User){
        return userDao.addUser(user)
    }
    fun getUsers(): LiveData<List<User>> {
        return userDao.readAllUsers()
    }

    suspend fun updateUser (user: User){
        return userDao.updateUser(user)
    }

}