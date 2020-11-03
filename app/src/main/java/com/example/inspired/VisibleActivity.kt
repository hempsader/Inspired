package com.example.inspired

import DailyQuote
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity

open class VisibleActivity : AppCompatActivity(){
    private val onShowNotification = object : BroadcastReceiver(){
        override fun onReceive(p0: Context?, p1: Intent?) {
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter =IntentFilter(DailyQuote.ACTION_SEND_NOTIF)
     //   registerReceiver(onShowNotification,filter,DailyQuote.PERM_PRIVATE,null)
    }

}