package com.example.inspired.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.ResponseQuoteRandom
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class QuoteViewModel(private val repository: QuoteRepositoryImpl,private val coroutineScope: CoroutineContext): ViewModel(){
    private val quoteMutableData = MutableLiveData<ResponseQuoteRandom>()
    private val quoteLocalMutableData = MutableLiveData<ResponseQuoteRandom>()


    fun observeRemoteQuote(): LiveData<ResponseQuoteRandom> = quoteMutableData
    fun observerLocalQuote(): LiveData<ResponseQuoteRandom> = quoteLocalMutableData




    fun fetchQuoteRemote(){
        viewModelScope.launch(context = coroutineScope) {
            try {
                val quoteResponse = repository.randomQuote()
                if (quoteResponse.isSuccessful) {
                    quoteMutableData.postValue(
                        ResponseQuoteRandom.ResponseSuccesfull(
                            quoteResponse.body()?.quote?.get(
                                0
                            )
                        )
                    )
                }
            } catch (e: Exception) {
                quoteMutableData.postValue(ResponseQuoteRandom.ResponseUnsuccessfull(e.stackTrace.toString()))
            }
        }
    }


    fun fetchLocalQuote() {
        viewModelScope.launch(context = coroutineScope) {
            if(repository.getQuoteRandomFromDb()?.size!! > 0 ) {
                quoteLocalMutableData.postValue(ResponseQuoteRandom.ResponseSuccesfull(repository.getQuoteRandomFromDb()?.random()))
            }else{
                quoteLocalMutableData.postValue(ResponseQuoteRandom.ResponseUnsuccessfull("No Quotes Catched!"))
            }
        }
    }

    fun insertOfflineQuote(quote:QuoteResponse.Quote?){
        viewModelScope.launch(context = coroutineScope) {
            repository.insertQuote(quote!!)
        }
    }

    fun favouriteQuote(quote: QuoteResponse.Quote?){
        viewModelScope.launch() {
            repository.updateQuote(quote!!)
        }
    }
}