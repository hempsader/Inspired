package com.ionut.grigore.inspired.viewModel.fetching

import android.app.Activity
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat

class BroadcastAppForeground : BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
       if(resultCode != Activity.RESULT_OK){
           return
       }
        val requestCode = intent?.getIntExtra(FetchingFirstTime.REQUEST_CODE, 0)
        val notification: Notification = intent?.getParcelableExtra(FetchingFirstTime.QUOTE)!!
         NotificationManagerCompat.from(context!!).apply {
            notify(requestCode!! , notification)
        }
    }

}