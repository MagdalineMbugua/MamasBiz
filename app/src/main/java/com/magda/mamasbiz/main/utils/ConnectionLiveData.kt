package com.magda.mamasbiz.main.utils

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConnectionLiveData(context: Context) : MutableLiveData<Boolean>() {
    private val TAG ="ConnectionLiveData"

    private val connectivityManager =
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val validNetworks: MutableSet<Network> = HashSet()

    override fun onActive() {
        Log.d(TAG, "onActive: called")
        networkCallback = createNetworkCallBack()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        super.onActive()
    }

    private fun createNetworkCallBack() = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            //Check for all networks
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            //Check if the network has internet capability
            val hasInternetCapabilities =
                networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)

            //Check if it has actual internet
            if (hasInternetCapabilities == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                    Log.d(TAG, "onAvailable: $hasInternet")
                    if(hasInternet){
                        withContext(Dispatchers.Main){
                            validNetworks.add(network)
                            checkValidNetworks()
                        }}

                }

            }
            super.onAvailable(network)
        }

        //If the network is lost, remove from the available networks
        override fun onLost(network: Network) {
            Log.d(TAG, "onLost: called")
            super.onLost(network)
            validNetworks.remove(network)
            checkValidNetworks()
        }
    }



    override fun onInactive() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
        Log.d(TAG, "onInactive: called")
        super.onInactive()
    }
    private fun checkValidNetworks() {
        postValue(validNetworks.size > 0)
    }
}