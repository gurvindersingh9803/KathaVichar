package com.example.kathavichar.common

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AndroidNetworkStatusProvider(context: Context) {

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    // Using StateFlow to manage network status (online/offline)
    private val _networkStatusFlow = MutableStateFlow(false) // Default is offline
    val networkStatusFlow: StateFlow<Boolean> = _networkStatusFlow

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.i("NetworkStatus", "Network Available")
            _networkStatusFlow.value = true // Update to online
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.i("NetworkStatus", "Network Lost")
            _networkStatusFlow.value = false // Update to online
        }
    }

    init {
        registerNetworkCallback() // Automatically start monitoring
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)    // Detect Wi-Fi
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR) // Detect Mobile Data
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun isConnected(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            connectivityManager.activeNetwork?.let { activeNetwork ->
                connectivityManager.getNetworkCapabilities(activeNetwork)?.let { networkCapabilities ->
                    return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                            networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }
            }
            return false
        } else {
            @Suppress("DEPRECATION")
            return connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}
