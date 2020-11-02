package com.example.inspired

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.concurrent.futures.CallbackToFutureAdapter
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.*

class DailyQuote(private val context: Context,private val workerParameters: WorkerParameters): ListenableWorker(context,workerParameters) {

    override fun startWork(): ListenableFuture<Result> {
        return CallbackToFutureAdapter.getFuture {
            val x = CoroutineScope(Dispatchers.IO).async {
                val quote = Repository.get()?.getRandomQuote()

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

                val notif =  NotificationCompat.Builder(context,"channelId")
                    .setSmallIcon(R.drawable.ic_baseline_settings_24)
                    .setContentTitle("Inspired Author: ${quote?.quoteAuthor}")
                    .setContentText(quote?.quoteText)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)



                with(NotificationManagerCompat.from(context)){
                    notify(0,notif.build())
                }
            }
            GlobalScope.launch(Dispatchers.Main){
                x.await()
            }
        }
    }

}

