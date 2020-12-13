package com.example.inspired.util

import com.example.inspired.model.QuoteResponse

sealed class ResponseQuoteRandom{
    data class ResponseSuccesfull(val quote: QuoteResponse.Quote?): ResponseQuoteRandom()
    data class ResponseUnsuccessfull(val string: String): ResponseQuoteRandom()
}