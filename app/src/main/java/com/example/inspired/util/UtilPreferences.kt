package com.example.inspired.util

import android.content.Context

class UtilPreferences {
    companion object{
        fun roomEnable(context: Context): Boolean = context.getSharedPreferences("roomEnable", Context.MODE_PRIVATE).getBoolean("roomEnable", true)
        fun roomEnableSet(context: Context, enable: Boolean){
            context.getSharedPreferences("roomEnable",Context.MODE_PRIVATE).edit().putBoolean("roomEnable",enable).apply()
        }

        fun dailyEnable(context: Context): Boolean = context.getSharedPreferences("dailyOn", Context.MODE_PRIVATE).getBoolean("dailyOn", true)
        fun dailyEnableSet(context: Context, daily: Boolean){
            context.getSharedPreferences("dailyOn", Context.MODE_PRIVATE).edit().putBoolean("dailyOn",daily).apply()
        }

        fun dailyHour(context: Context): Int = context.getSharedPreferences("dailyHour", Context.MODE_PRIVATE).getInt("dailyHour", 9)
        fun dailyHourSet(context: Context, hour: Int){
            context.getSharedPreferences("dailyHour", Context.MODE_PRIVATE).edit().putInt("dailyHour", Context.MODE_PRIVATE).putInt("dailyHour",hour).apply()

        }
    }

}