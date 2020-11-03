package com.example.inspired.repository

import android.content.Context
import com.example.inspired.model.Quote
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Repository private constructor(context: Context){
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
    fun getExecutor(): ExecutorService =  Executors.newFixedThreadPool(1)

    fun questionFetch(fetch: (quote: Quote)-> Unit) {
        repositoryFetch.fetchQuote {
            fetch(it)
        }
    }
    suspend fun getRandomQuote(): Quote = respositoryRoom.getRandomQuote()
    fun insertQuote(quote: Quote) {
        respositoryRoom.insertQuote(quote)
    }
}