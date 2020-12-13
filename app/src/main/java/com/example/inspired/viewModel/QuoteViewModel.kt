package com.example.inspired.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.ResponseQuoteRandom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class QuoteViewModel(private val repository: QuoteRepositoryImpl,private val coroutineScope: CoroutineContext): ViewModel(){
    private val quoteMutableData = MutableLiveData<ResponseQuoteRandom>()


    fun observeRemoteQuote(): LiveData<ResponseQuoteRandom> = quoteMutableData

    fun fetchQuoteRemote(){
        GlobalScope.launch(context = coroutineScope) {
            val quoteResponse = repository.randomQuote()
            if(quoteResponse.isSuccessful){
                quoteMutableData.postValue(ResponseQuoteRandom.ResponseSuccesfull(quoteResponse.body()?.quote?.get(0)))
            }else{
                quoteMutableData.postValue(ResponseQuoteRandom.ResponseUnsuccessfull("Ups, something went wrong, fetching offline!"))
            }
        }

    }
}