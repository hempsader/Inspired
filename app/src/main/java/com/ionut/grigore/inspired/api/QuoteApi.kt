package com.ionut.grigore.inspired.api



import com.ionut.grigore.inspired.model.QuoteResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.concurrent.TimeUnit

interface QuoteApi{

    companion object{
        private fun client() = OkHttpClient.Builder()
            .readTimeout(7, TimeUnit.SECONDS)
            .connectTimeout(7, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
        fun get(): QuoteApi = Retrofit.Builder()
            .baseUrl("https://quote-garden.herokuapp.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client())
            .build().create(QuoteApi::class.java)
    }

    @GET("api/v3/quotes/random")
    suspend fun getRandomQuote(): Response<QuoteResponse>

}