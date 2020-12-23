package com.example.inspired.notif

import android.content.Context
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class QuoteNotifWorker(private val context: Context, private val params: WorkerParameters) : Worker(context,params){
    override fun doWork(): Result {
        TODO("Not yet implemented")
    }
}