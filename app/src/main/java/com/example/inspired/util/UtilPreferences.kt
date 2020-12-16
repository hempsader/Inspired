package com.example.inspired.util

import android.content.Context

class UtilPreferences {
    companion object{
        fun isLoaded(context: Context): Boolean = context.getSharedPreferences("isLoaded", Context.MODE_PRIVATE).getBoolean("isLoaded", true)

        fun saveLoad(context: Context, isLoaded: Boolean){
            context.getSharedPreferences("isLoaded",Context.MODE_PRIVATE).edit().putBoolean("isLoaded",isLoaded).apply()
        }
    }

}