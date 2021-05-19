package com.magda.mamasbiz.main.utils

import android.app.Application
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.magda.mamasbiz.main.userInterface.activities.OtpActivity

  class ConnectivityManager(application: Application) {
    private val connectionLiveData = ConnectionLiveData(application)
      private val TAG = "Connectivity Manager"

    // observe this in ui
    val isNetworkAvailable = MutableLiveData(false)
    fun registerConnectionObserver(lifecycleOwner: LifecycleOwner){
        connectionLiveData.observe(lifecycleOwner, { isConnected ->
            isConnected?.let {
                isNetworkAvailable.value = it
                Log.d(TAG, "registerConnectionObserver: $isConnected")
                Log.d(TAG, "registerConnectionObserver: $isNetworkAvailable")
            }
        })
    }

    fun unregisterConnectionObserver(lifecycleOwner: LifecycleOwner){
        connectionLiveData.removeObservers(lifecycleOwner)
    }
}