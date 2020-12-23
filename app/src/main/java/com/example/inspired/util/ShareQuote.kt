package com.example.inspired.util

import android.content.Context
import android.content.Intent
import com.example.inspired.model.QuoteResponse

class ShareQuote(private val context: Context) {

    fun quote(quote: QuoteResponse.Quote){
        val shareQuote = Intent().apply{
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, quote.text + " - " + quote.author)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(shareQuote,"Inspired Quote"))
    }

}