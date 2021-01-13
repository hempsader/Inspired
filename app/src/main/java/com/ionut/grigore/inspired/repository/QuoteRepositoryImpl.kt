package com.ionut.grigore.inspired.repository

import com.ionut.grigore.inspired.api.QuoteApi
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.room.QuoteDatabase
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class QuoteRepositoryImpl: QuoteRepository{
    private val dao = QuoteDatabase.instanceGet()?.quoteDao()

    override suspend fun insertQuote(quote: QuoteResponse.Quote) {
        dao?.insert(quote)
    }

    override  fun getFavourites(): Flow<List<QuoteResponse.Quote>> {
        return dao?.getFavourites()!!
    }


    override suspend fun randomQuote(): Response<QuoteResponse> {
        return QuoteApi.get().getRandomQuote()
    }



    override suspend fun getQuoteRandomFromDb(): List<QuoteResponse.Quote>? {
        return dao?.randomQuoteDbPull()
    }

    override suspend fun updateQuote(quote: QuoteResponse.Quote) {
        dao?.updateFavourite(quote)
    }

    override  fun fetchAll(): Flow<List<QuoteResponse.Quote>> {
      return  dao?.fetchAll()!!
    }



}

