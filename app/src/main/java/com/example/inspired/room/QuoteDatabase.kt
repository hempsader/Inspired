package com.example.inspired.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inspired.model.QuoteResponse

@Database(entities = [QuoteResponse.Quote::class], version = 1)
abstract class QuoteDatabase: RoomDatabase() {
    lateinit var quoteDao: QuoteDao

    companion object{
        @Volatile
         private var db: QuoteDatabase? =  null

        fun instantiate(context: Context): QuoteDatabase {
            return db ?: synchronized(this) {
                val instance = Room.databaseBuilder(context.applicationContext, QuoteDatabase::class.java, "quote_database").build()
                db = instance
                instance
            }
        }
    }
}