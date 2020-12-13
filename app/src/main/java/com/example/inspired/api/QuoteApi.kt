package com.example.inspired.api


import com.example.inspired.model.QuoteResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface QuoteApi{

    companion object{
        fun get(): QuoteApi = Retrofit.Builder()
            .baseUrl("https://quote-garden.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(QuoteApi::class.java)
    }

    @GET("api/v3/quotes/random")
    suspend fun getRandomQuote(): Response<QuoteResponse>

}