package com.ionut.grigore.inspired.viewModel.fetching

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationFavBroadcast : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val repo = QuoteRepositoryImpl()
        val scope = CoroutineScope(Dispatchers.IO + Job())
        if(intent?.extras != null){
            val id = intent.getStringExtra("id")
            val author = intent.getStringExtra("author")
            val text = intent.getStringExtra("text")
            val category = intent.getStringExtra("category")
            val quote = QuoteResponse.Quote(id!!,text!!,author!!, true, category!!)
            scope.launch {
                repo.insertQuote(quote)
            }
            Toast.makeText(context, R.string.inserted_favourite, Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, R.string.error_insert_favourite, Toast.LENGTH_SHORT).show()
        }

    }

}