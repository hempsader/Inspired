package com.example.inspired

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

class Util {
    fun isInternetConnected(context: Context): Boolean{
        var state = false
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connManager.registerDefaultNetworkCallback(object: ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                state = true
            }
            override fun onLost(network: Network) {
                super.onLost(network)
                state = false
            }
        })
        return state
    }
}