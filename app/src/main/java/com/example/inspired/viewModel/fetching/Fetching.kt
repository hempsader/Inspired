package com.example.inspired.viewModel.fetching

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.InternetUtil
import com.example.inspired.util.State
import com.example.inspired.util.UtilPreferences
import com.example.inspired.view.NOTIFICATION_CHANNEL_ID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest

class Fetching(private val context: Context, private val params: WorkerParameters): CoroutineWorker(context,params) {
    @InternalCoroutinesApi
    override suspend fun doWork(): Result {
        try {
                fetchQuote()
                return Result.success()
        }catch (e: Exception){
            if(runAttemptCount > 3){
                return Result.failure()
            }
        }finally {
            NotificationWorkStart.start(applicationContext, UtilPreferences.dailyHour(applicationContext), UtilPreferences.dailyMinute(applicationContext))
        }
        return Result.success()
    }
    @InternalCoroutinesApi
    private fun fetchQuote() {
        val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        val repository = QuoteRepositoryImpl(context)
        when (checkInternet()) {
            true -> {
                try {
                    coroutineScope.launch {
                        val remoteQuote = repository.randomQuote()
                        if (remoteQuote.isSuccessful) {
                            val quote = remoteQuote.body()
                            if (quote?.quote?.get(0)?.text?.isNotBlank()!! && quote?.quote?.get(0).author?.isNotBlank()) {
                                broadcastSentQuote(
                                    0,
                                    NotificationWorkStart.notif(quote.quote[0], applicationContext)
                                )
                            }
                        }
                    }
                } catch (e: Exception) {

                } finally {
                    coroutineScope.launch {
                        val quote = repository.getQuoteRandomFromDb()?.random()
                        broadcastSentQuote(
                            0,
                            NotificationWorkStart.notif(quote!!, applicationContext)
                        )
                    }
                }
            }
            false -> {
                coroutineScope.launch {
                    val quote = repository.getQuoteRandomFromDb()?.random()
                    broadcastSentQuote(0, NotificationWorkStart.notif(quote!!, applicationContext))
                }
            }
        }
    }
    private fun checkInternet(): Boolean{
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!
    }
    private fun notif(quote: QuoteResponse.Quote){
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setTicker("Inspire You")
            .setSmallIcon(android.R.drawable.ic_menu_report_image)
            .setContentTitle(quote.author)
            .setContentText(quote.text)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(quote.text))
            .setAutoCancel(true)
            .build()
        val notifManager = NotificationManagerCompat.from(context)
        notifManager.notify(0, notification)
    }

    private fun broadcastSentQuote(requestCode: Int, notification: Notification){
        val intent = Intent(FetchingFirstTime.ACTION_SHOW_NOTIFICATION).apply {
            putExtra(FetchingFirstTime.REQUEST_CODE, requestCode)
            putExtra(FetchingFirstTime.QUOTE, notification)
        }
        applicationContext.sendOrderedBroadcast(intent, FetchingFirstTime.PERM_PRIVATE)
    }

    companion object{
        const val ACTION_SHOW_NOTIFICATION = "com.example.inspired.SHOW_NOTIFICATION"
        const val PERM_PRIVATE = "com.example.inspired.PRIVATE"
        const val REQUEST_CODE = "REQUEST_CODE"
        const val QUOTE = "QUOTE"
    }
}