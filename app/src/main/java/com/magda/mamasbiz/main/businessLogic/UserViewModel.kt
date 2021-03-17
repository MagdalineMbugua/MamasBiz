package com.magda.mamasbiz.main.businessLogic

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magda.mamasbiz.main.data.Repository
import com.magda.mamasbiz.main.data.dao.UserDao
import com.magda.mamasbiz.main.data.database.MamaBizDatabase
import com.magda.mamasbiz.main.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application): ViewModel() {
    private val userRepository: Repository
    private val userDao: UserDao
    init {
        userDao=MamaBizDatabase.getDatabase(application).userDao()
        userRepository= Repository(null, userDao)
    }
    fun adduser(user: User){
      viewModelScope.launch(Dispatchers.IO) {
          userRepository.addUser(user)
      }
    }
}