package com.example.inspired.repository

import androidx.lifecycle.MutableLiveData
import com.example.inspired.api.QuoteFetch
import com.example.inspired.model.Quote

class RepositoryFetch(){
    private val quoteFetch: QuoteFetch
    init {
        quoteFetch = QuoteFetch()
    }
    fun fetchQuote(): MutableLiveData<Quote> = quoteFetch.fetchQuote()
}