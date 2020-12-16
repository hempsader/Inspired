package com.example.inspired

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.nio.channels.Channel

class ViewModelTest(val context: Context) :ViewModel(){
    val observeNet = MutableLiveData<Boolean>()
    val channel = kotlinx.coroutines.channels.Channel<Boolean>()
    init {
        GlobalScope.launch {
            haveNet()
                .collect {
                    Log.d("aa", it.toString())
                }
        }
    }

    @ExperimentalCoroutinesApi
    private fun haveNet(): Flow<Boolean> {
        val connMan = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = connMan.activeNetworkInfo
       return flow<Boolean> {
          emit(netInfo != null && netInfo.isConnectedOrConnecting)
        }
    }
}