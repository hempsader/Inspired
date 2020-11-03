package com.example.inspired.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspired.model.Quote
import com.example.inspired.repository.Repository

class QuoteViewModel : ViewModel(){
    var quoteViewModel: MutableLiveData<Quote>
    init {
        quoteViewModel = MutableLiveData()
        fetchQuote()
    }

    fun fetchQuote(){
            Repository.get()?.questionFetch {
                quoteViewModel.value = it
            }
    }

}