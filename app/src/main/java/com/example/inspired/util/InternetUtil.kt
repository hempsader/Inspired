package com.example.inspired.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlin.coroutines.CoroutineContext

class InternetUtil() {


    companion object {
        private var stopInternetCheck = false

        fun checkInternet(  context: Context, coroutineContext: CoroutineContext) = callbackFlow<State> {
            stopInternetCheck = !stopInternetCheck
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val state =
                manager.activeNetwork != null && manager.activeNetworkInfo?.isConnected!!
            launch(coroutineContext) {
                when (state) {
                    true -> {
                        if (stopInternetCheck) send(State.CONNECTED)
                    }
                    else -> {
                        if (stopInternetCheck) send(State.DISSCONNECTED)
                    }
                }
            }

            manager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    launch(coroutineContext) {
                        if (stopInternetCheck) {
                            send(State.CONNECTED)
                        }
                    }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    launch(coroutineContext) {
                        if (stopInternetCheck) {
                            send(State.DISSCONNECTED)
                        }
                    }
                }

                override fun onUnavailable() {
                    launch(coroutineContext) {
                        if (stopInternetCheck) {
                            send(State.DISSCONNECTED)
                        }
                    }
                }

                override fun onLost(network: Network) {
                    launch(coroutineContext) {
                        if (stopInternetCheck) {
                            send(State.DISSCONNECTED)
                        }
                    }
                }
            })
            awaitClose{
                if (isClosedForSend == false) {
                    close()
                }
            }
        }
        fun cancelInternetCheck() {
            stopInternetCheck = false
        }
    }



}

enum class State {
    CONNECTED,
    DISSCONNECTED
}