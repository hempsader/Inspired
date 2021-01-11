package com.ionut.grigore.inspired.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import com.ionut.grigore.inspired.model.QuoteResponse
import java.io.File

@Database(entities = [QuoteResponse.Quote::class], version = 31)
abstract class QuoteDatabase : RoomDatabase(){
    abstract fun quoteDao(): QuoteDao

    companion object{
        private var INSTANCE: QuoteDatabase? = null
        fun instance(context: Context): QuoteDatabase? {
            synchronized(Any()){
                return if(INSTANCE == null) {
                    val db = Room.databaseBuilder(context,QuoteDatabase::class.java,"quote_db")
                    //    .createFromAsset("quote_db")
                        .fallbackToDestructiveMigration().build()
                    INSTANCE = db
                    INSTANCE
                }else{
                    INSTANCE
                }
            }
        }
        fun instanceGet() = INSTANCE
    }
}