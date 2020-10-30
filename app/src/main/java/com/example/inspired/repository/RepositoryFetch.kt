package com.example.inspired.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.inspired.api.QuoteFetch
import com.example.inspired.model.Quote

class RepositoryFetch(){
    private val quoteFetch: QuoteFetch
    init {
        quoteFetch = QuoteFetch()
    }
    fun fetchQuote(fetch :(quote: Quote)-> Unit) {
        val mutableLiveData: MutableLiveData<Quote> = MutableLiveData()
        quoteFetch.fetchQuote {
            fetch(it)
        }
    }
}