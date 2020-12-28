package com.example.inspired.view

import android.app.AlertDialog
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.inspired.util.InternetUtil
import com.example.inspired.util.UtilPreferences
import com.example.inspired.viewModel.fetching.NotificationWorkStart
import com.judemanutd.autostarter.AutoStartPermissionHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.random.Random


const val NOTIFICATION_CHANNEL_ID = "quote"

class GlobalApp : Application() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
       val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        InternetUtil.initialise(applicationContext, applicationScope)
        InternetUtil.registerBroadCast()
        firstTimeRunNotif()
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

    private fun firstTimeRunNotif(){
        if(UtilPreferences.scheduleNewWork(applicationContext)) {
            UtilPreferences.scheduleNewWorkSet(applicationContext, false)
            UtilPreferences.dailyMinuteSet(applicationContext,0)
            val randomHour = Random.nextInt(8, 20)
            UtilPreferences.dailyHourSet(applicationContext, randomHour)
            if(UtilPreferences.dailyEnable(applicationContext)) {
                NotificationWorkStart.cancelOneTime(applicationContext)
                NotificationWorkStart.start(
                    applicationContext,
                    UtilPreferences.dailyHour(applicationContext),
                    UtilPreferences.dailyMinute(applicationContext)
                )
            }
        }
    }
}