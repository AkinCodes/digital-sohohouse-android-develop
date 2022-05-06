package com.sohohouse.seven.network.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

object NetworkUtils {

    fun isNetworkConnected(context: Context): Boolean {
        val activeNetwork = getActiveNetwork(context)
        return activeNetwork != null && activeNetwork.isConnected
    }

    private fun getActiveNetwork(context: Context): NetworkInfo? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo
    }

}