package com.example.inspired.viewModel.fetching

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class NotificationFavBroadcast : BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val repo = QuoteRepositoryImpl(context!!)
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
            Toast.makeText(context, "Inserted to favourite", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Opps...something went wrong!", Toast.LENGTH_SHORT).show()
        }

    }

}