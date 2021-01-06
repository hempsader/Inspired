package com.example.inspired.util

import android.content.Context
import android.util.Log
import com.example.inspired.model.QuoteResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object UnfavouriteFlow  {
    private val flowStr  = ConflatedBroadcastChannel<QuoteResponse.Quote>()

         suspend fun emitFavourite(quote: QuoteResponse.Quote) {
            flowStr?.send(quote)
            }

         fun readFavourite() = flowStr

}