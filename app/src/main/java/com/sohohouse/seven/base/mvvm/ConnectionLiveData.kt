package com.sohohouse.seven.base.mvvm

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

class ConnectionLiveData(val context: Context) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val connectivityReceiver: ConnectivityReceiver by lazy {
        ConnectivityReceiver()
    }

    private val callback: ConnectivityManager.NetworkCallback by lazy {
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                updateConnection(true)
            }

            override fun onLost(network: Network) {
                updateConnection(false)
            }

            override fun onUnavailable() {
                updateConnection(false)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onActive() {
        super.onActive()
        updateConnection()
        registerNetworkCallback()
    }

    @SuppressLint("ObsoleteSdkInt")
    override fun onInactive() {
        super.onInactive()
        unregisterNetworkCallback()
    }

    private fun registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(callback)
        } else {
            val request = NetworkRequest.Builder()
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(android.net.NetworkCapabilities.TRANSPORT_WIFI)
                .build()
            connectivityManager.registerNetworkCallback(request, callback)
        }
    }

    private fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(callback)
    }


    private fun updateConnection() {
        updateConnection(connectivityManager.activeNetworkInfo?.isConnected == true)
    }

    private fun updateConnection(connected: Boolean) {
        if (value == null && connected) return
        if (value != connected) {
            postValue(connected)
        }
    }

    private inner class ConnectivityReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            updateConnection()
        }
    }
}