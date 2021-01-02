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
import androidx.preference.ListPreference
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
import okhttp3.internal.Util
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
            private lateinit var hour: Preference
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            val room = findPreference<SwitchPreferenceCompat>("room")
             hour = findPreference<Preference>("hour")!!
            val daily = findPreference<SwitchPreferenceCompat>("daily_quote")
            val batterySaver = findPreference<Preference>("battery_saver")
            val autostart = findPreference<Preference>("autostart")
            val sortList = findPreference<ListPreference>("sortBy_list")

            room?.setOnPreferenceChangeListener { preference, newValue ->
                if(room.isChecked){
                    room.isChecked = false
                    UtilPreferences.roomEnableSet(requireContext(),false)
                }else{
                    room.isChecked = true
                    UtilPreferences.roomEnableSet(requireContext(),true)
                }
                true
            }

           hour?.setOnPreferenceClickListener {
               attention(requireContext())
               true
           }

            hour?.title = formatNotificationTime(UtilPreferences.dailyHour(requireContext()), UtilPreferences.dailyMinute(requireContext()))
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
            sortList?.title = sortTextString(UtilPreferences.sortType(requireContext()))
            sortList?.setOnPreferenceChangeListener { preference, newValue ->
                    UtilPreferences.sortTypeSet(requireContext(), sortTextInt(newValue as String))
                    sortList?.title = sortTextString(UtilPreferences.sortType(requireContext()))
                true
            }
            if(!daily?.isChecked!!){
                hour?.isEnabled = false
                autostart?.isEnabled = false
                batterySaver?.isEnabled = false
            }else{
                hour?.isEnabled = true
                autostart?.isEnabled = true
                batterySaver?.isEnabled = true
            }

            daily?.setOnPreferenceChangeListener { preference, newValue ->
                if(daily?.isChecked){
                    daily.isChecked = false
                    hour?.isEnabled = false
                    autostart?.isEnabled = false
                    batterySaver?.isEnabled = false
                    UtilPreferences.dailyEnableSet(requireContext(),false)
                    NotificationWorkStart.cancelFetchJob(requireContext())
                }else{
                    daily.isChecked = true
                    hour?.isEnabled = true
                    autostart?.isEnabled = true
                    batterySaver?.isEnabled = true
                    UtilPreferences.dailyEnableSet(requireContext(),true)
                    NotificationWorkStart.cancelFetchJob(requireContext())
                    NotificationWorkStart.start(requireContext(),UtilPreferences.dailyHour(requireContext()), UtilPreferences.dailyMinute(requireContext()))
                }
                true
            }
        }
        private fun attention(context: Context){
             AlertDialog.Builder(context)
                .setMessage("In order to conserve battery life and not to pressure server, notifications will not arrive at exact time that you choose!")
                .setNeutralButton("Dismiss") { dialog, _ ->
                    dialog.dismiss()
                    TimePick(hour).show(parentFragmentManager,"setHour")
                }.show()
        }

        private fun formatNotificationTime(hour: Int, minute: Int): String {
            val hour = if (hour < 10) "0${hour}" else "$hour"
            val minute = if (minute < 10) "0${minute}" else "$minute"
            return "Notification time around: $hour:$minute"
        }

        private fun sortTextInt(sort: String): Int {
           return when(sort){
                "Category" -> {
                   0
                }
               "Text" -> {
                  1
               }
               "Author" -> {
                  2
               }
               else -> 0
           }
        }
        private fun sortTextString(sort: Int): String{
            return when(sort){
                CATEGORY -> "Sort by Category"
                TEXT -> "Sort by Text"
                AUTHOR -> "Sort by Author"
                else -> "Sort by Category"
            }
        }

         class TimePick(private val time: Preference): DialogFragment(), TimePickerDialog.OnTimeSetListener {

            override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
                val c = Calendar.getInstance()
                val hour = c.get(Calendar.HOUR_OF_DAY)
                val minute = c.get(Calendar.MINUTE)
                return TimePickerDialog(activity, this, hour,minute , true)
            }

            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                UtilPreferences.dailyHourSet(requireContext(), hourOfDay)
                UtilPreferences.dailyMinuteSet(requireContext(), minute)
                val hour = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
                val minute = if (minute < 10) "0${minute}" else "${minute}"
                time.title = "Notification time around: $hour:$minute"
                if(!UtilPreferences.scheduleNewWork(requireContext())) {
                    NotificationWorkStart.cancelFetchJob(requireContext())
                    NotificationWorkStart.start(requireContext(),UtilPreferences.dailyHour(requireContext()), UtilPreferences.dailyMinute(requireContext()))
                }
            }
        }
    }

}