package com.example.inspired.api

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET


interface QuoteApi{
    @GET("api/v2/quotes/random")
    fun fetchRandomQuestion(): Call<JsonObject>
}