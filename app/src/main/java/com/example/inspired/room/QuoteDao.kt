package com.example.inspired.room

import androidx.room.*
import com.example.inspired.model.QuoteResponse

@Dao
interface QuoteDao{
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quote: QuoteResponse.Quote)

    @Query("select * from quote_db where favourite = 1")
    suspend fun getFavourites(): List<QuoteResponse.Quote>

    @Query("select * from quote_db")
    suspend fun randomQuoteDbPull(): List<QuoteResponse.Quote>

    @Delete
    suspend fun delete(quote: QuoteResponse.Quote)

}