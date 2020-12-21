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
    private val quoteLocalMutableData = MutableLiveData<QuoteResponse.Quote>()
    private val quoteFetchTest = MutableLiveData<List<QuoteResponse.Quote>>()


    fun observeRemoteQuote(): LiveData<ResponseQuoteRandom> = quoteMutableData
    fun observerLocalQuote(): LiveData<QuoteResponse.Quote> = quoteLocalMutableData
    fun test(): LiveData<List<QuoteResponse.Quote>> = quoteFetchTest

    init {
        fetchTest()

    }


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
                quoteMutableData.postValue(ResponseQuoteRandom.ResponseUnsuccessfull(e.stackTraceToString()))
            }
        }
    }

    private fun fetchTest(){
        viewModelScope.launch(context = coroutineScope) {
            repository.fetchAll().collect {
                quoteFetchTest.postValue(it)
            }
        }
    }

    fun fetchLocalQuote() {
        viewModelScope.launch(context = coroutineScope) {
            quoteLocalMutableData.postValue(repository.getQuoteRandomFromDb()?.random())
        }
    }

    fun insertOfflineQuote(quote:QuoteResponse.Quote?){
        viewModelScope.launch(context = coroutineScope) {
            repository.insertQuote(quote!!)
        }
    }

    fun favouriteQuote(quote: QuoteResponse.Quote?){
        viewModelScope.launch(context = coroutineScope) {
            repository.updateQuote(quote!!)
        }
    }
}