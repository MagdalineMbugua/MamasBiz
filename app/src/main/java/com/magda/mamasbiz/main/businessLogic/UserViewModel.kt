package com.magda.mamasbiz.main.businessLogic

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.magda.mamasbiz.main.data.repository.UserRepository
import com.magda.mamasbiz.main.data.dao.UserDao
import com.magda.mamasbiz.main.data.database.MamaBizDatabase
import com.magda.mamasbiz.main.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userUserRepository: UserRepository


    init {
        val userDao: UserDao = MamaBizDatabase.getDatabase(application).userDao()
        userUserRepository= UserRepository( userDao)
    }
    fun addUser(user: User){
      viewModelScope.launch(Dispatchers.IO) {
          userUserRepository.addUser(user)
      }
    }

}