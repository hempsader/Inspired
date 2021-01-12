package com.ionut.grigore.inspired.util

import android.util.Log
import com.ionut.grigore.inspired.model.QuoteResponse
import com.ionut.grigore.inspired.repository.QuoteRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.random.Random

class TmpGenerateDB {

       fun   generateList(listQuotes: (ArrayList<QuoteResponse.Quote>) -> Unit){
       
    }
    private fun generateId(): String {
        val range = "abcdefghijklmnoprstuzxyz1234567890"
        var string = ""
        for(i in 1..25){
            string += range.random()
        }

        return string
    }
    private fun generateText(): String{
        val range = "abcdefghijklmnoprstuzxyz1234567890"
        var string = ""
        for (i in 1..Random.nextInt(30)){
            for(j in 2..Random.nextInt(13)){
                string += range.random()
            }
            string += " "
        }
        return string
    }
    private fun generateAuthor(): String{
        val range = "abcdefghijklmnoprstuzxyz1234567890"
        var string = ""
        for (i in 1..2){
            for(j in 2..Random.nextInt(7)){
                string += range.random()
            }
            string += " "
        }
        return string
    }
}
