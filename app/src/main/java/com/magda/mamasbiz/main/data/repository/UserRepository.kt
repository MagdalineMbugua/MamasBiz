package com.magda.mamasbiz.main.data.repository

import com.magda.mamasbiz.main.data.dao.CreditDebtDao
import com.magda.mamasbiz.main.data.dao.UserDao
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.User

class UserRepository( private val userDao: UserDao){

    fun addUser (user: User){
        return userDao.addUser(user)
    }

}