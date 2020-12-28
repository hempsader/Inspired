package com.example.inspired.viewModel.fetching

import android.app.Notification
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.example.inspired.model.QuoteResponse
import com.example.inspired.view.NOTIFICATION_CHANNEL_ID
import java.util.*
import java.util.concurrent.TimeUnit

class NotificationWorkStart {
    companion object{
        fun start(context: Context, hour: Int, minute: Int){
            val currentDate = Calendar.getInstance()
            val dueDate = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            if(dueDate.before(currentDate)){
                dueDate.add(Calendar.HOUR_OF_DAY, 24)
            }
            val timeDiff = dueDate.timeInMillis - currentDate.timeInMillis
            Log.d("ee", timeDiff.toString())
            val dailyWork = OneTimeWorkRequestBuilder<FetchingFirstTime>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag("firstWork")
                .build()
            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork("firstWork", ExistingWorkPolicy.KEEP,dailyWork)
        }

        fun startPeriodic(context: Context){
            val dailyWork = PeriodicWorkRequestBuilder<Fetching>(1, TimeUnit.HOURS).build()
            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork("periodicWork", ExistingPeriodicWorkPolicy.KEEP, dailyWork)
        }

        fun cancelOneTime(context: Context){
            WorkManager.getInstance(context.applicationContext).cancelAllWorkByTag("firstWork")
        }

         fun notif(quote: QuoteResponse.Quote, context: Context): Notification{
            return  NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker("Inspire You")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(quote.author)
                .setContentText(quote.text)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                    .bigText(quote.text))
                .setAutoCancel(true)
                .build()
        }
    }
}