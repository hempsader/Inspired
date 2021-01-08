package com.ionut.grigore.inspired.util

import com.ionut.grigore.inspired.model.QuoteResponse


sealed class ResponseQuoteRandom{
    data class ResponseSuccesfull(val quote: QuoteResponse.Quote?): ResponseQuoteRandom()
    data class ResponseUnsuccessfull(val string: String): ResponseQuoteRandom()
}