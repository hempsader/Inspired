package com.ionut.grigore.inspired.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class QuoteFavouriteViewModel(private val repository: QuoteRepositoryImpl, private val coroutineScope: CoroutineContext) : ViewModel(){
     val favouriteMutableLiveData: LiveData<List<QuoteResponse.Quote>>
                get() = repository.getFavourites().flowOn(Dispatchers.Main)
                    .asLiveData()


    fun  favouriteUpdate(quote: QuoteResponse.Quote){
        viewModelScope.launch(context = coroutineScope) {
            repository.updateQuote(quote)
        }
    }


}