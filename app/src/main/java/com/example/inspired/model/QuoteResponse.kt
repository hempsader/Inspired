package com.example.inspired.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName


data class QuoteResponse(@SerializedName("quote") val quote: Quote) {

    @Entity(tableName = "quotes")
    data class Quote(
        @SerializedName("_id") val id: String,
        @SerializedName("quoteText") val text: String,
        @SerializedName("quoteAuthor") val author: String
    )

}