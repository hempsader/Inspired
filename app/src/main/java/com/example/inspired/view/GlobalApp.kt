package com.example.inspired.view

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
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
        if( AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(applicationContext)){
            if(AutoStartPermissionHelper.getInstance().getAutoStartPermission(applicationContext)){
                Log.d("dd", "success")
            }else{
                Log.d("dd", "failure")
            }
        }else{
            Log.d("dd", "bad")
        }
        if(UtilPreferences.scheduleNewWork(applicationContext)) {
            UtilPreferences.dailyMinuteSet(applicationContext,0)
            val randomHour = Random.nextInt(8, 20)
            UtilPreferences.dailyHourSet(applicationContext, randomHour)
            UtilPreferences.scheduleNewWorkSet(applicationContext, false)
            if(UtilPreferences.dailyEnable(applicationContext)) {
                Log.d("dd", "asdasda")
                NotificationWorkStart.cancelOneTime(applicationContext)
                NotificationWorkStart.start(
                    applicationContext,
                    UtilPreferences.dailyHour(applicationContext),
                    UtilPreferences.dailyMinute(applicationContext)
                )
            }
        }
       Log.d("ee", WorkManager.getInstance(applicationContext).getWorkInfosByTag("work").get().size.toString())
      //  AutoStartPermissionHelper.getInstance().getAutoStartPermission(applicationContext)
        notif()
    }

    private fun notif(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Inspired"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID,name,importance)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

}