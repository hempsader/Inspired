package com.ionut.grigore.inspired.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class InternetUtil {
    companion object {
        @ExperimentalCoroutinesApi
        private val flow = ConflatedBroadcastChannel<State>()
        private fun initialiseConnectivity(context: Context): Boolean {
            if(context != null) {
                val manager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                return manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!
            }
           return false
        }
        private var context: Context? = null
        private var coroutineScope: CoroutineScope? = null

        private object broadcast: BroadcastReceiver() {
            @ExperimentalCoroutinesApi
            override fun onReceive(context: Context?, intent: Intent?) {
                if (context != null) {
                    when (checkInternetBroadcast(context)) {
                        State.CONNECTED -> {
                            coroutineScope?.launch(Dispatchers.IO) {
                                flow.send(State.CONNECTED)
                            }
                        }
                        State.DISSCONNECTED -> {
                            coroutineScope?.launch(Dispatchers.IO) {
                                flow.send(State.DISSCONNECTED)
                            }
                        }
                    }
                }
            }
        }

        fun initialise(context: Context, coroutineScope: CoroutineScope): InternetUtil? {
            var internetUtil: InternetUtil? = null
            this.context = context
            this.coroutineScope = coroutineScope
            synchronized(Any()){
                if (internetUtil == null){
                    val net = InternetUtil()
                    internetUtil = net
                }else{
                    return internetUtil
                }
            }
            checking(Companion.context!!, Companion.coroutineScope!!)
            return internetUtil
        }


        fun registerBroadCast() {
            if (Build.VERSION.SDK_INT < 24) {
                context?.registerReceiver(
                    broadcast,
                    IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
                )
            }
        }



        @FlowPreview
        @ExperimentalCoroutinesApi
        @InternalCoroutinesApi
        fun getState(): Flow<State> {
            return flow.asFlow()
        }

        @ExperimentalCoroutinesApi
        private fun checking(context: Context, coroutineScope: CoroutineScope) {
            if (Build.VERSION.SDK_INT >= 24) {
                checkInternetFlow(context, coroutineScope)
            }
            if (Build.VERSION.SDK_INT < 24) {
                checkInternetBroadcast(context)
            }
        }

        //Api >= 24
        @ExperimentalCoroutinesApi
        @RequiresApi(Build.VERSION_CODES.N)
        private fun checkInternetFlow(
            context: Context,
            coroutineScope: CoroutineScope
        ) {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            manager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    coroutineScope.launch(Dispatchers.IO) {
                        flow.send(State.CONNECTED)
                    }
                }

                override fun onLost(network: Network) {
                    coroutineScope.launch(Dispatchers.IO) {
                        flow.send(State.DISSCONNECTED)
                    }
                }
            })
        }

        //Api <24
        private fun checkInternetBroadcast(context: Context): State {
            return if (initialiseConnectivity(context)) State.CONNECTED else State.DISSCONNECTED
        }


    }
}

enum class State {
    CONNECTED,
    DISSCONNECTED
}

