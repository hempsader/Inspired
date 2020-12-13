package com.example.inspired.repository

import android.content.Context
import com.example.inspired.api.QuoteApi
import com.example.inspired.model.QuoteResponse
import com.example.inspired.room.QuoteDatabase
import retrofit2.Response

class QuoteRepositoryImpl(context: Context): QuoteRepository{
    private val dao = QuoteDatabase.instance(context)?.quoteDao()

    override suspend fun insertQuote(quote: QuoteResponse.Quote) {
        dao?.insert(quote)
    }

    override suspend fun getFavourites(): List<QuoteResponse.Quote> {
        TODO("Not yet implemented")
    }

    override suspend fun randomQuote(): Response<QuoteResponse> {
      return QuoteApi.get().getRandomQuote()
    }

    override suspend fun deleteFavQuote(quote: QuoteResponse.Quote) {
        TODO("Not yet implemented")
    }

    override suspend fun getQuoteRandomFromDb(): QuoteResponse.Quote {
        TODO("Not yet implemented")
    }

}