package com.example.inspired.util

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.system.Os
import android.util.Log
import androidx.annotation.RequiresApi
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlin.coroutines.CoroutineContext

class InternetUtil() {



    companion object {


        @RequiresApi(Build.VERSION_CODES.N)
        fun checkInternet(context: Context, coroutineContext: CoroutineContext) =
            callbackFlow<State> {
                val manager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val state =
                    manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!
                launch(coroutineContext) {
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
                        launch(coroutineContext) {
                            send(State.CONNECTED)
                        }
                    }

                    override fun onLosing(network: Network, maxMsToLive: Int) {
                        launch(coroutineContext) {
                            send(State.DISSCONNECTED)
                        }
                    }

                    override fun onUnavailable() {
                        launch(coroutineContext) {
                            send(State.DISSCONNECTED)
                        }
                    }

                    override fun onLost(network: Network) {
                        launch(coroutineContext) {
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

