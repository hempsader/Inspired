package com.ionut.grigore.inspired.view


import android.util.Log
import android.view.View
import com.ionut.grigore.inspired.model.QuoteResponse
import java.util.*

fun sort(sortType: Int, quoteList: ArrayList<QuoteResponse.Quote>) {
        when (sortType) {
            AUTHOR -> {
                quoteList.sortWith(Comparator { old, new ->
                    old.author.compareTo(new.author)
                })
            }
            TEXT -> {
                quoteList.sortWith(Comparator { old, new ->
                    old.text.compareTo(new.text)
                })
            }
            CATEGORY -> {
                quoteList.sortWith(Comparator { old, new ->
                    old.category.compareTo(new.category)
                })
            }
        }
}

fun View.setClickDebounce(interval: Long, action : (View) -> Unit){
    var  lastTimeClick = System.currentTimeMillis()
    setOnClickListener {
        if(interval + lastTimeClick < System.currentTimeMillis()){
            lastTimeClick = System.currentTimeMillis()
            action(it)
        }
    }
}


const val CATEGORY = 0
const val TEXT = 1
const val AUTHOR = 2

