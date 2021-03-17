package com.magda.mamasbiz.main.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magda.mamasbiz.main.data.entity.User

interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: User)

    @Query("SELECT* From Credit_Debt_Table Order By creditDebtId ASC")
    fun readAllUsers(): LiveData<List<User>>
}