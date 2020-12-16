package com.example.inspired.repository

import com.example.inspired.model.QuoteResponse
import retrofit2.Response

interface QuoteRepository {
    suspend fun insertQuote(quote: QuoteResponse.Quote)
    suspend fun getFavourites(): List<QuoteResponse.Quote>?
    suspend fun randomQuote(): Response<QuoteResponse>
    suspend fun deleteFavQuote(quote: QuoteResponse.Quote)
    suspend fun getQuoteRandomFromDb(): List<QuoteResponse.Quote>?
    suspend fun updateQuote(quote: QuoteResponse.Quote)
}