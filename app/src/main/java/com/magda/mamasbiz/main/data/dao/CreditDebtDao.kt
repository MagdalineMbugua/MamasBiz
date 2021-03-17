package com.magda.mamasbiz.main.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.magda.mamasbiz.main.data.entity.CreditDebt

@Dao
interface CreditDebtDao {
    @Insert(onConflict =OnConflictStrategy.IGNORE)
    fun addCreditDebt(creditDebt: CreditDebt)

    @Query("SELECT* From Credit_Debt_Table Order By creditDebtId ASC")
    fun readAllCreditDebtData(): LiveData<List<CreditDebt>>


    @Update
    fun updateCreditDebt (creditDebt: CreditDebt)


    @Delete
    fun deleteCreditDebt(creditDebt: CreditDebt)
}