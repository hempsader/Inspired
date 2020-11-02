package com.example.inspired.util

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class Util(private val context: Context) {
    fun isInternetConnected(): Boolean{
        val nm = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = nm.activeNetwork
        val capabilities = nm.getNetworkCapabilities(currentNetwork)
        return  capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
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