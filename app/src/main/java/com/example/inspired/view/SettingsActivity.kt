package com.example.inspired.view

import android.app.Activity
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.inspired.R
import com.example.inspired.util.PowerOptimisationForNotif
import com.example.inspired.util.UtilPreferences
import com.example.inspired.viewModel.fetching.Fetching
import com.example.inspired.viewModel.fetching.FetchingFirstTime
import com.example.inspired.viewModel.fetching.NotificationWorkStart
import com.judemanutd.autostarter.AutoStartPermissionHelper
import java.util.*

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

    private val onShowNotification = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
             resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(Fetching.ACTION_SHOW_NOTIFICATION)
        application.registerReceiver(onShowNotification,filter, Fetching.PERM_PRIVATE,null)
    }

    override fun onStop() {
        super.onStop()
        application.unregisterReceiver(onShowNotification)
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
            val hour = findPreference<Preference>("hour")
            val daily = findPreference<SwitchPreferenceCompat>("daily_quote")
            val batterySaver = findPreference<Preference>("battery_saver")
            val autostart = findPreference<Preference>("autostart")
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

           hour?.setOnPreferenceClickListener {
               attention(requireContext())
               true
           }
            batterySaver?.isVisible = false
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                batterySaver?.isVisible = true
                batterySaver?.setOnPreferenceClickListener {
                    PowerOptimisationForNotif.disableBatterySaverForThisApp(requireContext(), true)
                    true
                }
            }
            autostart?.isVisible = false
            if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(requireContext())) {
                autostart?.isVisible = true
                 autostart?.setOnPreferenceClickListener {
                    PowerOptimisationForNotif.enableAutoStart(requireContext(), true)
                    true
                }
            }

            daily?.setOnPreferenceChangeListener { preference, newValue ->
                if(daily?.isChecked){
                    daily.isChecked = false
                    hour?.isEnabled = false
                    UtilPreferences.dailyEnableSet(requireContext(),false)
                }else{
                    daily.isChecked = true
                    hour?.isEnabled = true
                    UtilPreferences.dailyEnableSet(requireContext(),true)
                }
                true
            }
        }
        private fun attention(context: Context){
             AlertDialog.Builder(context)
                .setMessage("In order to not push pressure on the server and conserve battery, notification will not be at exact time that you choose!")
                .setNeutralButton("Dismiss") { dialog, which ->
                    dialog.dismiss()
                    TimePick.show(parentFragmentManager,"setHour")
                }.show()
        }

        object TimePick: DialogFragment(), TimePickerDialog.OnTimeSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                return TimePickerDialog(activity, this, hour,minute , true)
            }

            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                UtilPreferences.dailyHourSet(requireContext(),hourOfDay)
                UtilPreferences.dailyMinuteSet(requireContext(), minute)
                if(!UtilPreferences.scheduleNewWork(requireContext())) {
             //       NotificationWorkStart.cancelOneTime(requireContext())
                    NotificationWorkStart.start(requireContext(),UtilPreferences.dailyHour(requireContext()), UtilPreferences.dailyMinute(requireContext()))
                }
            }

        }
    }
}