package com.example.inspired.viewModel

import android.util.Log
import androidx.lifecycle.*
import com.example.inspired.model.QuoteResponse
import com.example.inspired.repository.QuoteRepository
import com.example.inspired.repository.QuoteRepositoryImpl
import com.example.inspired.util.UtilPreferences
import com.example.inspired.view.sort
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
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