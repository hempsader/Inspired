package com.ionut.grigore.inspired.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import com.ionut.grigore.inspired.R

import com.judemanutd.autostarter.AutoStartPermissionHelper

object PowerOptimisationForNotif {
        fun enableAutoStart(context: Context, firstTime: Boolean) {
            if (firstTime) {
                        AlertDialog.Builder(context)
                            .setTitle(R.string.autostart_dialog_title)
                            .setMessage(R.string.autostart_dialog_message)
                            .setOnCancelListener {
                                val ctx = context as Activity
                                ctx.finishAffinity()
                            }
                            .setPositiveButton(R.string.autostart_dialog_button) { dialog, _ ->
                                UtilPreferences.scheduleNewWorkSet( false)
                                dialog.dismiss()
                                if(!AutoStartPermissionHelper.getInstance().getAutoStartPermission(context)){
                                    if(AutoStartPermissionHelper.getInstance().getAutoStartPermission(context)){
                                        Toast.makeText(context, R.string.autostart_toast_enable, Toast.LENGTH_LONG).show()
                                    }else{
                                        Toast.makeText(context, R.string.autostart_toast_disabled, Toast.LENGTH_LONG).show()
                                    }
                                }
                            }.show()
            }
        }
        @SuppressLint("BatteryLife")
        fun disableBatterySaverForThisApp(context: Context, firstTime: Boolean){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (firstTime) {
                        AlertDialog.Builder(context)
                            .setTitle(R.string.battery_saver_title)
                            .setMessage(R.string.battery_saver_message)
                            .setOnCancelListener {
                               val ctx = context as Activity
                                ctx.finishAffinity()
                            }
                            .setNeutralButton(R.string.battery_saver_dismiss) { dialog, _ ->
                                if (!AutoStartPermissionHelper.getInstance()
                                        .isAutoStartPermissionAvailable(context)
                                ){
                                    UtilPreferences.scheduleNewWorkSet( false)
                                }
                                dialog.dismiss()
                                val intent = Intent()
                                val packageName = context.packageName
                                val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                                    intent.action =
                                        Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                                    intent.data = Uri.parse("package:$packageName")
                                    context.startActivity(intent)
                                }
                            }.show()
                    }
                }
        }
}