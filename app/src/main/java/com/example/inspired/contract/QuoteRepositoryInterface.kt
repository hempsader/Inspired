package com.example.inspired.contract

import com.example.inspired.model.QuoteResponse
import com.example.inspired.room.QuoteDao
import kotlinx.coroutines.flow.Flow

interface QuoteRepositoryInterface {

    fun insertQuote(quote: QuoteDao)
    fun getQuotes(): Flow<List<QuoteResponse.Quote>>
}