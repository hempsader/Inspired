package com.example.inspired.api

import android.util.Log
import com.example.inspired.model.Quote
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class QuoteFetch {
    private fun fetchRandomQuote(): Retrofit {
        return Retrofit.Builder().baseUrl("https://quote-garden.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun fetchQuote(quoteValue :(quote: Quote) -> Unit) {
        fetchRandomQuote().create(QuoteApi::class.java).fetchRandomQuestion().enqueue(object:
            Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val jsonOBj = response.body()?.get("quote")
                val quote = Quote(jsonOBj?.asJsonObject?.get("_id")!!.toString(),
                jsonOBj.asJsonObject["quoteText"]!!.toString(), jsonOBj.asJsonObject["quoteAuthor"]!!.toString())
               quoteValue(quote)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.d("aa",t.message.toString())
            }

        })
    }

    fun fetchQuoteAsync(): Call<JsonObject> {
       return fetchRandomQuote().create(QuoteApi::class.java).fetchRandomQuestion()
    }
}