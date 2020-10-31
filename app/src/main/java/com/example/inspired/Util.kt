package com.example.inspired

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.hardware.camera2.params.Capability
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Bundle
import android.telecom.ConnectionService
import android.util.Log
import androidx.core.app.DialogCompat
import androidx.fragment.app.DialogFragment

class Util(private val context: Context) {
    fun isInternetConnected(): Boolean{
        val nm = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = nm.activeNetwork
        val capabilities = nm.getNetworkCapabilities(currentNetwork)
        return  capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }

    fun internetMonitoring() : Boolean {
        var isAvaiable = true
        Log.d("aa",isAvaiable.toString())
        val networkManager = context.getSystemService(ConnectivityManager::class.java)
        networkManager.registerDefaultNetworkCallback(object: ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
               isAvaiable = true
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                isAvaiable = false
            }
        })
        return  isAvaiable
    }

     fun dialogNoInternet(){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
        builder.apply {
            setTitle("No Internet Connection!")
                .setMessage("No Internet Connection!, switching to offline mode!")
            setCancelable(false)
            setNegativeButton("OK",DialogInterface.OnClickListener { dialogInterface, i ->
                dialogInterface.cancel()
            })
        }
        builder.create().show()
    }
}