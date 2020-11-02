package com.example.inspired

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository
import kotlinx.coroutines.*

class DailyQuote(private val context: Context,private val workerParameters: WorkerParameters): Worker(context,workerParameters) {
    override fun doWork(): Result {
        with(NotificationManagerCompat.from(context)){
            notify(0,deliverDailyQuote().build())
        }
    }

    private fun deliverDailyQuote():NotificationCompat.Builder{
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val name = "Inspired"
            val descriptionText = "Inspired Quote"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channelId",name,importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context,0,intent,0)
         return NotificationCompat.Builder(context,"channelId")
            .setSmallIcon(R.drawable.ic_baseline_settings_24)
            .setContentTitle("Inspired Author: ${getAuthor()}")
            .setContentText(getQuote())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
    }
    private fun getAuthor(): String? {
        var author: String? = null
        val x = CoroutineScope(Dispatchers.IO).async {
            author = Repository.get()?.getRandomQuote()?.quoteAuthor
        }
        GlobalScope.launch(Dispatchers.Main){
            x.await()
        }
        return author!!
    }

    private fun getQuote(): String? {
        var quote: String? = null
        val x = CoroutineScope(Dispatchers.IO).async {
            quote = Repository.get()?.getRandomQuote()?.quoteText
        }
        GlobalScope.launch(Dispatchers.Main){
            x.await()
        }
        return quote!!
    }
}