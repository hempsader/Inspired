package com.ionut.grigore.inspired.viewModel.fetching

import android.app.Notification
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl

import kotlinx.coroutines.*

class FetchingFirstTime(private val context: Context, workerParameters: WorkerParameters): CoroutineWorker(context,workerParameters) {
    @RequiresApi(Build.VERSION_CODES.O)
    @InternalCoroutinesApi
    override suspend fun doWork(): Result {
        try {
                fetchQuote()
                NotificationWorkStart.startPeriodic(applicationContext)
               return Result.success()

        }catch (e: Exception){
            if(runAttemptCount > 3){
                return Result.failure()
            }
        }
        return Result.retry()
    }

    @InternalCoroutinesApi
    private fun fetchQuote(){
        val coroutineScope = CoroutineScope(Job() + Dispatchers.IO)
        val repository = QuoteRepositoryImpl()
        when (checkInternet()) {
            true -> {
                try {
                    coroutineScope.launch {
                        val remoteQuote = repository.randomQuote()
                        if (remoteQuote.isSuccessful) {
                            val quote = remoteQuote.body()
                            if (quote?.quote?.get(0)?.text?.isNotBlank()!! && quote.quote[0].author.isNotBlank()) {
                                broadcastSentQuote(
                                    0,
                                    NotificationWorkStart.notif(quote.quote[0], applicationContext)
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    coroutineScope.launch {
                        val quote = repository.getQuoteRandomFromDb()?.random()
                        broadcastSentQuote(0, NotificationWorkStart.notif(quote!!, applicationContext))
                    }
                }
            }
            false -> {
                coroutineScope.launch {
                    try {
                        val quote = repository.getQuoteRandomFromDb()?.random()
                        broadcastSentQuote(
                            0,
                            NotificationWorkStart.notif(quote!!, applicationContext)
                        )
                    }catch (e: Exception){
                        broadcastSentQuote(0,
                        NotificationWorkStart.notifEmptyDB(applicationContext))
                    }
                }
            }
        }
        }

    private fun checkInternet(): Boolean{
        val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return manager.activeNetworkInfo != null && manager.activeNetworkInfo?.isConnected!!
    }


    private fun broadcastSentQuote(requestCode: Int, notification: Notification){
        val intent = Intent(ACTION_SHOW_NOTIFICATION).apply {
            putExtra(REQUEST_CODE, requestCode)
            putExtra(QUOTE, notification)
        }
        applicationContext.sendOrderedBroadcast(intent, PERM_PRIVATE)
    }

        companion object{
            const val ACTION_SHOW_NOTIFICATION = "com.example.inspired.SHOW_NOTIFICATION"
            const val PERM_PRIVATE = "com.example.inspired.PRIVATE"
            const val REQUEST_CODE = "REQUEST_CODE"
            const val QUOTE = "QUOTE"
    }
}