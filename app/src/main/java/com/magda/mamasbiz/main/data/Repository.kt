package com.magda.mamasbiz.main.data

import com.magda.mamasbiz.main.data.dao.CreditDebtDao
import com.magda.mamasbiz.main.data.dao.UserDao
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.User

class Repository(private val creditDebtDao: CreditDebtDao?, private val userDao: UserDao){
    val readAllCreditDebt = creditDebtDao.readAllCreditDebtData()
    fun addCreditDebt(creditDebt: CreditDebt){
        return creditDebtDao.addCreditDebt(creditDebt)
    }
    fun addUser (user: User){
        return userDao.addUser(user)
    }

}