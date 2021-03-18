package com.magda.mamasbiz.main.data.repository

import com.magda.mamasbiz.main.data.dao.CreditDebtDao
import com.magda.mamasbiz.main.data.entity.CreditDebt

class CreditDebtRepository (private val creditDebtDao: CreditDebtDao) {
    val readAllCreditDebt = creditDebtDao.readAllCreditDebtData()


    fun addCreditDebt(creditDebt: CreditDebt){
        return creditDebtDao.addCreditDebt(creditDebt)
    }
}