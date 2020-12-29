package com.example.inspired.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.inspired.model.QuoteResponse

@Database(entities = [QuoteResponse.Quote::class], version = 28)
abstract class QuoteDatabase : RoomDatabase(){
    abstract fun quoteDao(): QuoteDao

    companion object{
        private var INSTANCE: QuoteDatabase? = null
        fun instance(context: Context): QuoteDatabase? {
            synchronized(Any()){
                if(INSTANCE == null) {
                    val db = Room.databaseBuilder(context,QuoteDatabase::class.java,"quote_db").fallbackToDestructiveMigration().build()
                    INSTANCE = db
                }
            }
            return INSTANCE
        }
    }
}