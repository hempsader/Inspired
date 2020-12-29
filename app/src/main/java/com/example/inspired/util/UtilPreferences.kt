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
            context.getSharedPreferences("dailyHour", Context.MODE_PRIVATE).edit().putInt("dailyHour",hour).apply()
        }

        fun dailyMinute(context: Context): Int = context.getSharedPreferences("dailyMinute", Context.MODE_PRIVATE).getInt("dailyMinute", 0)
        fun dailyMinuteSet(context: Context, minute: Int){
            context.getSharedPreferences("dailyMinute", Context.MODE_PRIVATE).edit().putInt("dailyMinute",minute).apply()
        }

        fun scheduleNewWork(context: Context): Boolean = context.getSharedPreferences("schedule", Context.MODE_PRIVATE).getBoolean("schedule",true)
        fun scheduleNewWorkSet(context: Context, schedule: Boolean){
            context.getSharedPreferences("schedule", Context.MODE_PRIVATE).edit().putBoolean("schedule", schedule).apply()
        }
    }

}