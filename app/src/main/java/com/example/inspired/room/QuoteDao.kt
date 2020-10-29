package com.example.inspired.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.inspired.model.Quote

@Dao
interface QuoteDao{
    @Query("select * from quote ")
    fun getAllQuotes(): List<Quote>

    @Query("select * from quote where _id like :id")
    fun findQuote(id: String): Quote

    @Insert
    fun insertQuote(quote: Quote)

}