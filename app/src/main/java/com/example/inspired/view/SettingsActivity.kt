package com.example.inspired.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.inspired.R
import com.example.inspired.util.UtilPreferences

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(
                    R.id.settings,
                    SettingsFragment()
                )
                .commit()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


    override fun onBackPressed() {
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }


    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val room = findPreference<SwitchPreferenceCompat>("room")
            val colors = findPreference<SwitchPreferenceCompat>("colors")
            val listHour = findPreference<ListPreference>("daily")
            val daily = findPreference<SwitchPreferenceCompat>("daily_quote")

            room?.setOnPreferenceChangeListener { preference, newValue ->
                if(room.isChecked){
                    room.isChecked = false
                    UtilPreferences.roomEnableSet(requireContext(),false)
                }else{
                    room.isChecked = false
                    UtilPreferences.roomEnableSet(requireContext(),true)
                }
                true
            }

            listHour?.setOnPreferenceChangeListener { preference, newValue ->
                if(listHour.key == "daily") {
                    try {
                        val hour = (listHour.value).toInt()
                        UtilPreferences.dailyHourSet(requireContext(), hour)
                    }catch (e: Exception){

                    }
                }
                true
            }


            daily?.setOnPreferenceChangeListener { preference, newValue ->
                if(daily?.isChecked){
                    daily.isChecked = false
                    listHour?.isEnabled = false
                    UtilPreferences.dailyEnableSet(requireContext(),false)
                }else{
                    daily.isChecked = true
                    listHour?.isEnabled = true
                    UtilPreferences.dailyEnableSet(requireContext(),true)
                }
                true
            }
        }
    }
}