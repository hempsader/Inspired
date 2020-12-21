package com.example.inspired.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.system.Os
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleCoroutineScope
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlin.coroutines.CoroutineContext

class InternetUtil(private val context: Context) {
    companion object {
        @RequiresApi(Build.VERSION_CODES.N)
        fun checkInternet(context: Context, lifecycleCoroutineScope: LifecycleCoroutineScope) =
            callbackFlow {
                val manager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val state =
                    manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!
                lifecycleCoroutineScope.launch(coroutineContext) {
                    when (state) {
                        true -> {
                            send(State.CONNECTED)
                        }
                        else -> {
                            send(State.DISSCONNECTED)
                        }
                    }
                }

                manager.registerDefaultNetworkCallback(object :
                    ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        lifecycleCoroutineScope.launch(coroutineContext) {
                            send(State.CONNECTED)
                        }
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        lifecycleCoroutineScope.launch(coroutineContext) {
                            send(State.DISSCONNECTED)
                        }
                    }

                    override fun onUnavailable() {
                        lifecycleCoroutineScope.launch(coroutineContext) {
                            send(State.DISSCONNECTED)
                        }
                    }

                    override fun onLost(network: Network) {
                        lifecycleCoroutineScope.launch(coroutineContext) {
                            send(State.DISSCONNECTED)
                        }
                    }
                })
                awaitClose {
                    if (isClosedForSend == false) {
                        close()
                    }
                }
            }


        fun checkInternetBroadcast(context: Context): State {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isOnline =
                manager.activeNetworkInfo != null && manager.activeNetworkInfo!!.isConnected
            return if (isOnline) State.CONNECTED else State.DISSCONNECTED
        }

    }
}

enum class State {
    CONNECTED,
    DISSCONNECTED
}

