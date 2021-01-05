package com.example.inspired.util

import android.content.Context
import android.util.Log
import com.example.inspired.model.QuoteResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

object UnfavouriteFlow  {
    private val flowStr  = ConflatedBroadcastChannel<String>()

         suspend fun emitFavourite(quoteId: String) {
            flowStr?.send(quoteId)
            }

         fun readFavourite() = flowStr

}