package com.magda.mamasbiz.main.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.magda.mamasbiz.main.data.dao.CreditDebtDao
import com.magda.mamasbiz.main.data.dao.UserDao
import com.magda.mamasbiz.main.data.entity.CreditDebt
import com.magda.mamasbiz.main.data.entity.User

@Database(entities = [CreditDebt::class,User::class],version = 1,exportSchema = true )
abstract class MamaBizDatabase: RoomDatabase() {
    abstract fun creditDebtDao():CreditDebtDao
    abstract fun userDao(): UserDao
    companion object{
        @Volatile
        private var INSTANCE: MamaBizDatabase? = null
        fun getDatabase(context: Context): MamaBizDatabase{
            val tempInstance = INSTANCE
            if(tempInstance!=null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MamaBizDatabase::class.java,
                    "MamaBiz_database"
                ).build()
                INSTANCE= instance
                return instance
            }

        }

    }
}