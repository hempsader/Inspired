package com.example.inspired.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.inspired.model.QuoteResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao{

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(quote: QuoteResponse.Quote)

    @Query("select * from quote_db where favourite = 1")
     fun getFavourites():  Flow<List<QuoteResponse.Quote>>

    @Query("select * from quote_db")
    suspend fun randomQuoteDbPull(): List<QuoteResponse.Quote>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateFavourite(quote: QuoteResponse.Quote)

    @Delete
    suspend fun delete(quote: QuoteResponse.Quote)

    @Query("select * from quote_db")
    fun fetchAll(): Flow<List<QuoteResponse.Quote>>

}