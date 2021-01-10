package com.ionut.grigore.inspired.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class QuoteResponse(@SerializedName("data") val quote: List<Quote>) {

    @Entity(tableName = "quote_db")
    data class Quote(
        @PrimaryKey
        @SerializedName("_id") val id: String = "xxx",
        @SerializedName("quoteText") val text: String = "Ooops...no quote fetched",
        @SerializedName("quoteAuthor") val author: String = "Unknown Author",
        var favourite: Boolean = false,
        @SerializedName("quoteGenre") val category: String = "Unknown"
    )

}