package com.ionut.grigore.inspired.repository

import com.ionut.grigore.inspired.model.QuoteResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface QuoteRepository {
    suspend fun insertQuote(quote: QuoteResponse.Quote)
     fun getFavourites(): Flow<List<QuoteResponse.Quote>>
    suspend fun randomQuote(): Response<QuoteResponse>
    suspend fun getQuoteRandomFromDb(): List<QuoteResponse.Quote>?
    suspend fun updateQuote(quote: QuoteResponse.Quote)
     fun fetchAll(): Flow<List<QuoteResponse.Quote>>

}