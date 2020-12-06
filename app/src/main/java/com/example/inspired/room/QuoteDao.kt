package com.example.inspired.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.inspired.model.QuoteResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertQuote(quote: QuoteDao)

    @Query("select * from quotes")
    fun getQuotes(): Flow<List<QuoteResponse.Quote>>
}