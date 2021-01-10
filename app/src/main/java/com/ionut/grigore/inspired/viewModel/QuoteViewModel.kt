package com.ionut.grigore.inspired.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ionut.grigore.inspired.R
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl
import com.ionut.grigore.inspired.util.ResponseQuoteRandom
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class QuoteViewModel(private val repository: QuoteRepositoryImpl, private val coroutineScope: CoroutineContext): ViewModel(){
    private val quoteMutableData = MutableLiveData<ResponseQuoteRandom>()
    private val quoteLocalMutableData = MutableLiveData<ResponseQuoteRandom>()


    fun observeRemoteQuote(): LiveData<ResponseQuoteRandom> = quoteMutableData
    fun observerLocalQuote(): LiveData<ResponseQuoteRandom> = quoteLocalMutableData


    fun fetchQuoteRemote() {
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
                    quoteMutableData.postValue(ResponseQuoteRandom.ResponseUnsuccessfull(e.message.toString()))
                }
            }
    }


    fun fetchLocalQuote() {
        viewModelScope.launch(context = coroutineScope) {
            if(repository.getQuoteRandomFromDb()?.size!! > 0 ) {
                quoteLocalMutableData.postValue(ResponseQuoteRandom.ResponseSuccesfull(repository.getQuoteRandomFromDb()?.random()))
            }else{
                quoteLocalMutableData.postValue(ResponseQuoteRandom.ResponseUnsuccessfull(R.string.no_quotes_cathed.toString()))
            }
        }
    }

    fun insertOfflineQuote(quote: QuoteResponse.Quote?){
        viewModelScope.launch(context = coroutineScope) {
            repository.insertQuote(quote!!)
        }
    }

    fun favouriteQuote(quote: QuoteResponse.Quote?){
        viewModelScope.launch {
            repository.updateQuote(quote!!)
        }
    }
}