package com.magda.mamasbiz.main.businessLogic.viewModels

import android.app.Application
import androidx.lifecycle.*
import com.magda.mamasbiz.main.data.repository.UserRepository
import com.magda.mamasbiz.main.data.entity.User
import com.magda.mamasbiz.main.data.responses.NetworkResponse
import com.magda.mamasbiz.main.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userRepository: UserRepository = UserRepository()
    private val addUserLiveData : MutableLiveData<NetworkResponse<Boolean>> =MutableLiveData<NetworkResponse<Boolean>>()
    val _addUserLiveData get() = addUserLiveData
    private val updateUserLiveData : MutableLiveData<NetworkResponse<Boolean>> =MutableLiveData<NetworkResponse<Boolean>>()
    val _updateUserLiveData get() = updateUserLiveData

    private val fetchUserLiveData : MutableLiveData<NetworkResponse<User>> =MutableLiveData<NetworkResponse<User>>()
    val _fetchUserLiveData get() = fetchUserLiveData


    fun addUser(user: User){
      viewModelScope.launch(Dispatchers.IO) {
          addUserLiveData.postValue(NetworkResponse.loading())
          userRepository.addUser(user){
              when(it){
                  is Results.Success -> {
                      addUserLiveData.postValue(NetworkResponse.success(true, null))
                  }
                  is Results.Error -> {
                      addUserLiveData.postValue(NetworkResponse.error(it.error))
                  }
              }
          }
      }
    }
    fun updateUser(userPassword: String, userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            updateUserLiveData.postValue(NetworkResponse.loading())
            userRepository.updateUser(userPassword, userId){
                when (it){
                    is Results.Success ->{
                        updateUserLiveData.postValue(NetworkResponse.success(true, null))
                    }
                    is Results.Error -> {
                        updateUserLiveData.postValue(NetworkResponse.error(it.error))
                    }

                }
            }
        }
    }
    fun fetchUser (userId: String){
        viewModelScope.launch(Dispatchers.IO){
            fetchUserLiveData.postValue(NetworkResponse.loading())
            userRepository.readUsers(userId){
                when (it){
                    is Results.Success -> {
                        fetchUserLiveData.postValue(NetworkResponse.success(true, it.data))
                    }
                    is Results.Error -> {
                        fetchUserLiveData.postValue(NetworkResponse.error(it.error))
                    }
                }
            }
        }
    }

}