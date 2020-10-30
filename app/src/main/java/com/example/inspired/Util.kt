package com.example.inspired

import android.content.Context
import android.hardware.camera2.params.Capability
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo

class Util {
    fun isInternetConnected(context: Context): Boolean{
        val nm = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = nm.activeNetwork
        val capabilities = nm.getNetworkCapabilities(currentNetwork)
        return  capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
}