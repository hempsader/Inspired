package com.example.inspired.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.inspired.api.QuoteApi
import com.example.inspired.model.QuoteResponse
import com.example.inspired.room.QuoteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import retrofit2.Response

class QuoteRepositoryImpl(context: Context): QuoteRepository{
    private val dao = QuoteDatabase.instance(context)?.quoteDao()

    override suspend fun insertQuote(quote: QuoteResponse.Quote) {
        dao?.insert(quote)
    }

    override fun getFavourites(): Flow<List<QuoteResponse.Quote>> {
        return dao?.getFavourites()!!
    }


    override suspend fun randomQuote(): Response<QuoteResponse> {
        return QuoteApi.get().getRandomQuote()
    }

    override suspend fun deleteFavQuote(quote: QuoteResponse.Quote) {
        TODO("Not yet implemented")
    }

    override suspend fun getQuoteRandomFromDb(): List<QuoteResponse.Quote>? {
        return dao?.randomQuoteDbPull()
    }

    override suspend fun updateQuote(quote: QuoteResponse.Quote) {
        dao?.updateFavourite(quote)
    }

    override suspend fun fetchAll(): Flow<List<QuoteResponse.Quote>> {
      return  dao?.fetchAll()!!
    }

}

