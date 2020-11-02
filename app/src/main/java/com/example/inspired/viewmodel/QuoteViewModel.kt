package com.example.inspired.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository

class QuoteViewModel() : ViewModel(){
    var quoteViewModel: MutableLiveData<Quote>
    init {
        quoteViewModel = MutableLiveData()
    }

    fun fetchQuote(){
            Repository.get()?.questionFetch {
                quoteViewModel.value = it
            }
    }
    fun getQuoteValue() = quoteViewModel.value
}