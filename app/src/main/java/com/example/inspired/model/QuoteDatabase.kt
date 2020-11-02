package com.example.inspired.model

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.inspired.room.QuoteDao


@Database(entities = arrayOf(Quote::class), version = 3)
abstract class QuoteDatabase: RoomDatabase() {
    abstract fun quoteDao(): QuoteDao
}