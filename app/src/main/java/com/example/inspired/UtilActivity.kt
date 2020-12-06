package com.example.inspired

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.inspired.util.InternetUtil
import com.example.inspired.util.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class UtilActivity : AppCompatActivity() {
    private var job: Job? = null
    private val broadcast = BroadCastInsternet()
    private val channel = ConflatedBroadcastChannel<State>()

    override fun onResume() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            job = GlobalScope.launch(Dispatchers.Unconfined) {
                InternetUtil.checkInternet(this@UtilActivity, Dispatchers.IO)
                    .collect {
                        channel.send(it)
                    }
            }
        } else {
            registerReceiver(
               broadcast,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
        if (Build.VERSION.SDK_INT < 24 ) {
            unregisterReceiver(broadcast)
        }
    }

    override fun onDestroy() {
        job?.cancel()
        job = null
        super.onDestroy()
    }

  inner  class BroadCastInsternet: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (context?.let { InternetUtil.checkInternetBroadcast(it)}) {
                State.CONNECTED -> {
                    GlobalScope.launch(Dispatchers.IO) {
                        channel.send(State.CONNECTED)
                    }
                }
                State.DISSCONNECTED -> {
                        GlobalScope.launch(Dispatchers.IO) {
                            channel.send(State.DISSCONNECTED)
                        }
                }
            }
        }

}
    fun internetFlow() = channel
}