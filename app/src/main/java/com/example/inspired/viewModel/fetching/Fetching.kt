package com.example.inspired.viewModel.fetching

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.inspired.R
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.InternetUtil
import com.example.inspired.util.ResponseQuoteRandom
import com.example.inspired.util.State
import com.example.inspired.view.NOTIFICATION_CHANNEL_ID
import com.example.inspired.viewModel.QuoteViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import java.util.*
import java.util.concurrent.TimeUnit

class Fetching(private val context: Context, private val workerParameters: WorkerParameters): Worker(context,workerParameters) {
    @InternalCoroutinesApi
    override fun doWork(): Result {
        fetchQuote()
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
        return Result.success()
    }

    @InternalCoroutinesApi
    private fun fetchQuote(){
        val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        val repository = QuoteRepositoryImpl(context)
        coroutineScope.launch {
            InternetUtil.getState().collectLatest {
                when(it){
                    State.CONNECTED -> {
                        val remoteQuote = repository.randomQuote()
                        if(remoteQuote.isSuccessful)
                        {
                            val quote = remoteQuote.body()
                            if(quote?.quote?.get(0)?.text?.isNotBlank()!! && quote?.quote?.get(0).author?.isNotBlank()){
                                notif(quote.quote[0])
                            }
                        }
                    }
                    State.DISSCONNECTED -> {
                        val quoate = repository.getQuoteRandomFromDb()?.random()
                        notif(quoate!!)
                    }
                }
            }
        }
    }
    private fun notif(quote: QuoteResponse.Quote){
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker("Inspire You")
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(quote.author)
            .setContentText(quote.text)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(quote.text))
            .setAutoCancel(true)
            .build()
        val notifManager = NotificationManagerCompat.from(context)
        notifManager.notify(0, notification)
    }
}