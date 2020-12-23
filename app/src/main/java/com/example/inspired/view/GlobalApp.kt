package com.example.inspired.view

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.*
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.InternetUtil
import com.example.inspired.viewModel.QuoteViewModel
import com.example.inspired.viewModel.fetching.Fetching
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.TimeUnit

const val NOTIFICATION_CHANNEL_ID = "quote"

class GlobalApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
        InternetUtil.initialise(applicationContext, applicationScope)
        InternetUtil.registerBroadCast()
        notif()
        val workRequest = PeriodicWorkRequest.Builder(
            Fetching::class.java,
            15, TimeUnit.MINUTES
        ).build()
        WorkManager.getInstance().enqueueUniquePeriodicWork("newImages",
            ExistingPeriodicWorkPolicy.REPLACE,workRequest)
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

    private fun scheduleWork(){
        val currentDate = Calendar.getInstance()
        val dueDate = Calendar.getInstance()
        dueDate.set(Calendar.HOUR, 21)
        dueDate.set(Calendar.MINUTE,30)
        dueDate.set(Calendar.SECOND, 0)
        if(dueDate.before(currentDate)){
            dueDate.add(Calendar.HOUR_OF_DAY, 24)
        }
        val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
        val dailyRequest = OneTimeWorkRequestBuilder<Fetching>()
            .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
            .addTag("TAG")
            .build()

        WorkManager.getInstance(applicationContext).enqueue(dailyRequest)
    }
}