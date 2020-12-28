package com.example.inspired.view

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.inspired.viewModel.fetching.Fetching

abstract class VisibleFragment: Fragment(){
    private val onShowNotification = object: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(requireContext(), "Got a broadcast", Toast.LENGTH_SHORT).show()
            resultCode = Activity.RESULT_CANCELED
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(Fetching.ACTION_SHOW_NOTIFICATION)
        requireActivity().registerReceiver(onShowNotification,filter,Fetching.PERM_PRIVATE,null)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(onShowNotification)
    }

}