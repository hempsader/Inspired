package com.example.inspired.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.internal.ChannelFlow
import kotlin.coroutines.CoroutineContext

class InternetUtil(private val context: Context): CoroutineScope {
    private val job = Job()
    val channel = BroadcastChannel<State>(1)

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    init {
        launch(coroutineContext) {
            val manager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            /*
             val state = manager.activeNetwork != null && manager.activeNetworkInfo?.isConnected!!
            when (state) {
                true -> offer(State.CONNECTED)
                else -> offer(State.DISSCONNECTED)
            }
         */
            manager.registerDefaultNetworkCallback(object :
                ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    if(!channel.isClosedForSend){
                        channel.offer(State.CONNECTED)
                    }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    if(!channel.isClosedForSend){
                        channel.offer(State.DISSCONNECTED)
                    }
                }

                override fun onUnavailable() {
                    if(!channel.isClosedForSend){
                        channel.offer(State.DISSCONNECTED)
                    }
                }

                override fun onLost(network: Network) {
                    if(!channel.isClosedForSend){
                        channel.offer(State.DISSCONNECTED)
                    }
                }
            })
        }
    }

    fun cancelInternetCheck() {
        channel.cancel()
        coroutineContext.cancel()
    }

    fun openChannel(): ReceiveChannel<State>{
       return  channel.openSubscription()
    }
}

enum class State {
    CONNECTED,
    DISSCONNECTED
}