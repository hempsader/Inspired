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
import androidx.core.app.DialogCompat
import androidx.fragment.app.DialogFragment

class Util(private val context: Context) {
    fun isInternetConnected(): Boolean{
        val nm = context.getSystemService(ConnectivityManager::class.java)
        val currentNetwork = nm.activeNetwork
        val capabilities = nm.getNetworkCapabilities(currentNetwork)
        return  capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ?: false
    }
    fun dialogIsNotInternet(){
         object : DialogFragment(){
            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val builder = AlertDialog.Builder(context)
                builder.setMessage("No Internet Connection, switching to offline mode!")
                    .setNeutralButton("Ok",DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                return builder.create()
            }
        }
    }
}