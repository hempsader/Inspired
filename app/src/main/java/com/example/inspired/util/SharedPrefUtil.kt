package com.example.inspired.util

import android.content.Context
import androidx.preference.PreferenceManager

class SharedPrefUtil {
    companion object {
        fun saveRoomEnable(context: Context,isRoomEnable: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean("isRoomOn",isRoomEnable).apply()
        }

        fun getRoomEnabled(context: Context): Boolean? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean("isRoomOn",true)
        }

        fun saveColorEnable(context: Context, isColorEnable: Boolean) {
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean("isColorOn", isColorEnable).apply()
        }

        fun getColorEnabled(context: Context): Boolean? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean("isColorOn",true)
        }

        fun saveDailyEnable(context: Context, isDailyEnable: Boolean){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean("isDailyOn", isDailyEnable).apply()
        }

        fun getDailyEnabled(context: Context): Boolean? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean("isDailyOn",true)
        }

        fun saveHourDaily(context: Context, hour: String){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("hourDaily",hour).apply()
        }
        fun getHourDaily(context: Context): String? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return  pref.getString("hourDaily","nine")
        }

        fun saveNotifAuthor(context: Context, author: String){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("author",author).apply()
        }

        fun saveNotifQuote(context: Context, quote: String){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putString("quote",quote).apply()
        }

        fun getNotifAuthor(context: Context): String? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getString("author","No Data Yet")
        }

        fun getNotifQuote(context: Context): String? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getString("quote","No Data Yet")
        }

        fun setClickedOnNotif(context: Context, clicked: Boolean){
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean("notifClick",clicked).apply()
        }

        fun getClickedOnNotif(context: Context): Boolean? {
            val pref = PreferenceManager.getDefaultSharedPreferences(context)
            return pref.getBoolean("notifClick",false)
        }
    }
}