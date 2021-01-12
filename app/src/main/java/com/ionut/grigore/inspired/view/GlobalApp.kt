package com.ionut.grigore.inspired.view


import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.ionut.grigore.inspired.room.QuoteDatabase
import com.ionut.grigore.inspired.util.UtilPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob


const val NOTIFICATION_CHANNEL_ID = "quote"

class GlobalApp : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
        QuoteDatabase.instance(applicationContext)
        UtilPreferences.context = applicationContext
        notif()

    }


    private fun notif(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Inspired"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,name,importance).apply {
                vibrationPattern = longArrayOf(500)
                setSound(Settings.System.DEFAULT_NOTIFICATION_URI, null)
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}