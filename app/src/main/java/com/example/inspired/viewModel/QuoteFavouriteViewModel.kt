package com.example.inspired.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepository
import com.example.inspired.repository.QuoteRepositoryImpl
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class QuoteFavouriteViewModel(private val repository: QuoteRepositoryImpl, private val coroutineScope: CoroutineContext) : ViewModel(){
    private val favouriteMutableLiveData = MutableLiveData<List<QuoteResponse.Quote>>()


    init {
        GlobalScope.launch(context = coroutineScope) {
            repository.getFavourites()?.asFlow()?.collect {

            }
        }
    }

    fun favouriteQuotes(): LiveData<List<QuoteResponse.Quote>> = favouriteMutableLiveData
}