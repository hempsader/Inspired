package com.example.inspired.util

import android.content.Context
import android.util.Log
import com.example.inspired.model.QuoteResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class UnfavouriteFlow (){
    companion object{
        private var unfavouriteFlow: UnfavouriteFlow? = null
        private val flow = ConflatedBroadcastChannel<String>()
        fun initialise(): UnfavouriteFlow{
           return if(unfavouriteFlow == null){
               unfavouriteFlow = UnfavouriteFlow()
               unfavouriteFlow!!
            }else{
               unfavouriteFlow!!
            }
        }
        fun emitFavourite(quote: QuoteResponse.Quote) {
            GlobalScope.launch {
            flow.send(quote.id)
            }
        }
        fun readFavourite() = channelFlow<String> {
            flow.asFlow()
        }
    }
}