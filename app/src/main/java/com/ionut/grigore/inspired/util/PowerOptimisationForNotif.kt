package com.ionut.grigore.inspired.util

import android.annotation.SuppressLint
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
                            .setPositiveButton(R.string.autostart_dialog_button) { dialog, _ ->
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
                            .setNeutralButton(R.string.battery_saver_dismiss) { dialog, _ ->
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