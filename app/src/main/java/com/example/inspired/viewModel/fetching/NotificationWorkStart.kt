package com.example.inspired.viewModel.fetching

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.example.inspired.model.QuoteResponse
import com.example.inspired.view.NOTIFICATION_CHANNEL_ID
import java.util.*
import java.util.concurrent.TimeUnit
import com.example.inspired.R
import com.example.inspired.view.FragmentFavourite
import com.example.inspired.view.FragmentRandom
import com.example.inspired.view.MainActivity

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
            val dailyWork = OneTimeWorkRequestBuilder<FetchingFirstTime>()
                .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                .addTag("work")
                .build()
            WorkManager.getInstance(context.applicationContext).beginUniqueWork("firstWork", ExistingWorkPolicy.KEEP,dailyWork).enqueue()
        }

        fun startPeriodic(context: Context){
            val dailyWork = PeriodicWorkRequestBuilder<Fetching>(1, TimeUnit.HOURS)
                .addTag("work")
                .build()
            WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork("periodicWork", ExistingPeriodicWorkPolicy.KEEP, dailyWork)
        }

        fun cancelOneTime(context: Context){
            WorkManager.getInstance(context.applicationContext).cancelAllWorkByTag("firstWork")
        }

         fun notif(quote: QuoteResponse.Quote, context: Context): Notification{
             val intentFavourite = Intent(context.applicationContext, NotificationFavBroadcast::class.java).apply {
                 putExtra("author", quote.author)
                 putExtra("text", quote.text)
                 putExtra("id", quote.id)
                 flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
             }
             val pendingIntentFav = PendingIntent.getBroadcast(context.applicationContext,0, intentFavourite,PendingIntent.FLAG_UPDATE_CURRENT)
             val appIntent =  Intent(context, MainActivity::class.java)
             val pendingIntentOpenApp = PendingIntent.getActivity(context.applicationContext, 0, appIntent,  PendingIntent.FLAG_UPDATE_CURRENT)


            return  NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setTicker("Inspire You")
                .setSmallIcon(android.R.drawable.ic_menu_report_image)
                .setContentTitle(quote.author)
                .setContentText(quote.text)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                    .bigText(quote.text))
                .addAction(R.drawable.ic_baseline_favorite_24_true,"Add to Favourite", pendingIntentFav)
                .setContentIntent(pendingIntentOpenApp)
                .setAutoCancel(true)
                .build()
        }
    }
}