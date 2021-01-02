package com.example.inspired.view

import com.example.inspired.model.QuoteResponse
import java.util.*

fun sort(sortType: Int, quoteList: List<QuoteResponse.Quote>) {
        when (sortType) {
            AUTHOR -> {
                Collections.sort(quoteList) { old, new ->
                    old.author.compareTo(new.category)
                }
            }
            TEXT -> {
                Collections.sort(quoteList) { old, new ->
                    old.text.compareTo(new.text)
                }
            }
            CATEGORY -> {
                Collections.sort(quoteList) { old, new ->
                    old.category.compareTo(new.category)
                }
            }
        }
}


const val CATEGORY = 0
const val TEXT = 1
const val AUTHOR = 2

