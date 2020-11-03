package com.example.inspired

import android.app.Activity
import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotifReciever : BroadcastReceiver(){
    override fun onReceive(p0: Context?, p1: Intent?) {
        if(resultCode != Activity.RESULT_OK ) return
        val requestCode = p1?.getIntExtra(DailyQuote.REQUEST_CODE,0)
        val notification: Notification = p1?.getParcelableExtra(DailyQuote.NOTIFICATION)!!
        val notificationManager = p0?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(requestCode!!,notification)
    }
}