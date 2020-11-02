package com.example.inspired

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.inspired.util.SharedPrefUtil
import kotlinx.android.synthetic.main.activity_main.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }




    class SettingsFragment : PreferenceFragmentCompat() {
        private lateinit var dailyEnable: SwitchPreferenceCompat
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_set, rootKey)
            val roomEnable: SwitchPreferenceCompat? = findPreference("room")
             dailyEnable = findPreference("daily_quote")!!
            val randomColor: SwitchPreferenceCompat? = findPreference("colors")
            val hourPref: ListPreference? = findPreference("daily")
            dailyEnable.setOnPreferenceChangeListener(//pune listener)
      }


    }


}