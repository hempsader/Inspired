package com.example.inspired.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspired.model.Quote
import java.lang.IllegalStateException

class Repository private constructor(private val context: Context){
    private var repositoryFetch: RepositoryFetch
    private var respositoryRoom: RepositoryRoom

    init {
        repositoryFetch = RepositoryFetch()
        respositoryRoom = RepositoryRoom(context)
    }

    companion object{
        private var initialise: Repository? = null
        fun init(context: Context){
            if(initialise == null){
                initialise = Repository(context)
            }
        }
        fun get() = initialise
    }

    fun questionFetch(fetch: (quote: Quote)-> Unit) {
        repositoryFetch.fetchQuote {
            fetch(it)
        }
    }
    fun getRandomQuote(): Quote = respositoryRoom.getRandomQuote()
    fun suffiecientQuote(): Boolean = respositoryRoom.sufficientQuotes()
    fun insertQuote(quote: Quote) {
        respositoryRoom.insertQuote(quote)
    }
}