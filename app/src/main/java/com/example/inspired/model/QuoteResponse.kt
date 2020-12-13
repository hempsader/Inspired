package com.example.inspired.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class QuoteResponse(@SerializedName("data") val quote: List<Quote>) {

    @Entity(tableName = "quote_db")
    data class Quote(
        @PrimaryKey(autoGenerate = true)
        val idRoom: Int,
        @SerializedName("_id") val id: String,
        @SerializedName("quoteText") val text: String,
        @SerializedName("quoteAuthor") val author: String,
        var favourite: Boolean = false
    )

}