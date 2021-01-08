package com.ionut.grigore.inspired.util


import com.ionut.grigore.inspired.model.QuoteResponse
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

object UnfavouriteFlow  {
    private val flowStr  = ConflatedBroadcastChannel<QuoteResponse.Quote>()

         suspend fun emitFavourite(quote: QuoteResponse.Quote) {
             flowStr.send(quote)
            }

         fun readFavourite() = flowStr

}