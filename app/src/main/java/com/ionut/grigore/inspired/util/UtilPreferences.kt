package com.ionut.grigore.inspired.util

import android.content.Context


object UtilPreferences {

        var context: Context? = null

        fun roomEnable(): Boolean = context?.getSharedPreferences("roomEnable", Context.MODE_PRIVATE)?.getBoolean("roomEnable", true)!!
        fun roomEnableSet(enable: Boolean){
            context?.getSharedPreferences("roomEnable",Context.MODE_PRIVATE)?.edit()?.putBoolean("roomEnable",enable)?.apply()
        }

        fun dailyEnable(): Boolean = context?.getSharedPreferences("dailyOn", Context.MODE_PRIVATE)?.getBoolean("dailyOn", true)!!
        fun dailyEnableSet(daily: Boolean){
            context?.getSharedPreferences("dailyOn", Context.MODE_PRIVATE)?.edit()?.putBoolean("dailyOn",daily)?.apply()
        }

        fun dailyHour(): Int = context?.getSharedPreferences("dailyHour", Context.MODE_PRIVATE)?.getInt("dailyHour", 9)!!
        fun dailyHourSet(hour: Int){
            context?.getSharedPreferences("dailyHour", Context.MODE_PRIVATE)?.edit()?.putInt("dailyHour",hour)?.apply()
        }

        fun dailyMinute(): Int = context?.getSharedPreferences("dailyMinute", Context.MODE_PRIVATE)?.getInt("dailyMinute", 0)!!
        fun dailyMinuteSet( minute: Int){
            context?.getSharedPreferences("dailyMinute", Context.MODE_PRIVATE)?.edit()?.putInt("dailyMinute",minute)?.apply()
        }

        fun scheduleNewWork(): Boolean = context?.getSharedPreferences("schedule", Context.MODE_PRIVATE)?.getBoolean("schedule",true)!!
        fun scheduleNewWorkSet(schedule: Boolean){
            context?.getSharedPreferences("schedule", Context.MODE_PRIVATE)?.edit()?.putBoolean("schedule", schedule)?.apply()
        }

        fun sortType(): Int = context?.getSharedPreferences("sortBy", Context.MODE_PRIVATE)?.getInt("sortBy",0)!!
        fun sortTypeSet( type: Int){
            context?.getSharedPreferences("sortBy",Context.MODE_PRIVATE)?.edit()?.putInt("sortBy",type)?.apply()
        }


        fun offlineFetch(): Boolean? = context?.getSharedPreferences("offlineFetch", Context.MODE_PRIVATE)?.getBoolean("offlineFetch", false)!!
        fun offlineFetchSet( offline: Boolean){
            context?.getSharedPreferences("offlineFetch", Context.MODE_PRIVATE)?.edit()?.putBoolean("offlineFetch", offline)?.apply()
        }

        fun termsAndConditions(): Boolean = context?.getSharedPreferences("termsAndConditions", Context.MODE_PRIVATE)?.getBoolean("termsAndConditions", false)!!
        fun termsAndConditions(accepted: Boolean){
            context?.getSharedPreferences("termsAndConditions", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("termsAndConditions",accepted)
                ?.apply()
        }

        fun tutorial(): Boolean = context?.getSharedPreferences("tutorial", Context.MODE_PRIVATE)?.getBoolean("tutorial", false)!!
        fun tutorialSet(done: Boolean){
            context?.getSharedPreferences("tutorial", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("tutorial", done)?.apply()
        }
        fun serverPressure(): Boolean = context?.getSharedPreferences("server", Context.MODE_PRIVATE)?.getBoolean("server",false)!!
        fun serverPressureSet(read: Boolean){
            context?.getSharedPreferences("server", Context.MODE_PRIVATE)?.edit()
                ?.putBoolean("server", read)?.apply()
        }

}