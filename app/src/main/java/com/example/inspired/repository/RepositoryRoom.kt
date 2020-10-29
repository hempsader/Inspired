package com.example.inspired.repository

import android.content.Context
import androidx.room.Room
import com.example.inspired.model.Quote
import com.example.inspired.model.QuoteDatabase
import java.util.concurrent.Executors

class RepositoryRoom(private val context: Context) {
    private val db = Room.databaseBuilder(context, QuoteDatabase::class.java,"quote_db").build()
    private val dao = db.quoteDao()
    private val executor = Executors.newSingleThreadExecutor()


    fun getRandomQuote(): Quote {
        var quote: Quote? = null
      executor.execute {
              quote = dao.getAllQuotes().shuffled().first()
      }
        return quote!!
    }

    fun sufficientQuotes(): Boolean{
        var sufficient = false
        executor.execute {
            if(dao.getAllQuotes().size >= 100) sufficient = true else sufficient = false
        }
        return sufficient
    }

    fun insertQuote(quote: Quote) {
        executor.execute {
            if(dao.findQuote(quote._id) == null) dao.insertQuote(quote)
        }
    }

}