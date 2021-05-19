package com.magda.mamasbiz.main.utils

import android.content.Context
import android.content.SharedPreferences
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.utils.Constants.Companion.FIRST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.IS_LOGGED_IN
import com.magda.mamasbiz.main.utils.Constants.Companion.LAST_NAME
import com.magda.mamasbiz.main.utils.Constants.Companion.PASSWORD
import com.magda.mamasbiz.main.utils.Constants.Companion.PHONE_NUMBER
import com.magda.mamasbiz.main.utils.Constants.Companion.SHARED_PREF
import com.magda.mamasbiz.main.utils.Constants.Companion.USER_ID

class SessionManager(context: Context) {
    private lateinit var sharedPreferences:SharedPreferences
    private lateinit var editor:SharedPreferences.Editor

    init {
      sharedPreferences = context.getSharedPreferences(SHARED_PREF, android.content.Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
        editor.apply()
    }


    fun storeInfo(userId:String, firstName: String, lastName:String, password:String,isLoggedIn:Boolean){
        editor.putString(USER_ID, userId)
        editor.putString(FIRST_NAME, firstName)
        editor.putString(LAST_NAME, lastName)
        editor.putString(PASSWORD, password)
        editor.putBoolean(IS_LOGGED_IN,true)
        editor.apply()



    }
    fun getUser(): User{
        val user =User ()
        user.userId =sharedPreferences.getString(USER_ID,"")
        user.firstName = sharedPreferences.getString(FIRST_NAME,"")
        user. lastName = sharedPreferences.getString(LAST_NAME,"")
        user. password = sharedPreferences.getString(PASSWORD,"")
        return user
    }
    fun getUserId():String?{
        return sharedPreferences.getString(USER_ID,"")
    }
    fun clearInfo(){
        editor.clear()
        editor.apply()

    }
    fun isLoggedIn():Boolean{
        return sharedPreferences.getBoolean(IS_LOGGED_IN,false)
    }
}
