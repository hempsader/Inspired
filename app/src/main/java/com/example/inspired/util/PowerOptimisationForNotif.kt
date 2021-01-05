package com.example.inspired.util

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.judemanutd.autostarter.AutoStartPermissionHelper

class PowerOptimisationForNotif {
    companion object{
        fun enableAutoStart(context: Context, firstTime: Boolean) {
            if (firstTime) {
                if(AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(context)) {
                    if (!AutoStartPermissionHelper.getInstance()
                            .getAutoStartPermission(context)
                    ) {
                        AlertDialog.Builder(context)
                            .setTitle("Enable Auto-Start")
                            .setMessage("In order to receive daily Quote notification, please enable Auto-Start. You also can change this setting in app settings. This setting will not affect battery life!")
                            .setPositiveButton("Enable") { dialog, _ ->
                                dialog.dismiss()
                                if(AutoStartPermissionHelper.getInstance().getAutoStartPermission(context)){
                                    Toast.makeText(context, "Auto-Start enabled!", Toast.LENGTH_LONG).show()
                                }else{
                                    Toast.makeText(context, "Auto-Start not enabled! Missing feature (NOTIF ON) or aborted by user (NOTIF OFF) ", Toast.LENGTH_LONG).show()
                                }
                            }.show()
                    }
                }
            }
        }
        fun disableBatterySaverForThisApp(context: Context, firstTime: Boolean){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (firstTime) {
                        AlertDialog.Builder(context)
                            .setTitle("Battery Saver")
                            .setMessage("In order to receive daily Quote notification when battery saver is ON, disable battery saver for this app. You can change this setting in app settings. After dismiss, pop-up for enable run in background should appear, if not, already enabled. This setting will not affect battery life!")
                            .setNeutralButton("Dismiss") { dialog, _ ->
                                dialog.dismiss()
                                val intent = Intent()
                                val packageName = context?.packageName
                                val pm =
                                    context?.getSystemService(Context.POWER_SERVICE) as PowerManager
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
}