package com.example.inspired.repository

import android.content.Context
import androidx.room.Room
import com.example.inspired.model.Quote
import com.example.inspired.model.QuoteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class RepositoryRoom(context: Context) {
    private val db = Room.databaseBuilder(context, QuoteDatabase::class.java,"quote_db").fallbackToDestructiveMigration().build()
    private val dao = db.quoteDao()


   suspend fun getRandomQuote(): Quote {
        var quote: Quote? = null
        val x = CoroutineScope(Dispatchers.IO).async {
            quote = dao.getAllQuotes().shuffled().first()
        }
        x.await()
       return quote!!
    }

    private fun returnQuote(): Quote {
       return dao.getAllQuotes().shuffled().first()
    }
    fun insertQuote(quote: Quote) {
        Repository.get()?.getExecutor()?.execute {
            dao.insertQuote(quote)
        }
    }

}