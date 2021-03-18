package com.magda.mamasbiz.main.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.magda.mamasbiz.main.data.entity.User
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addUser(user: User)

    @Query("SELECT* From User_Table Order By dateCreated ASC")
    fun readAllUsers(): LiveData<List<User>>
}