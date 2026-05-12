package com.mkarshnas6.karenstudio.worldskill.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class StopForegroundServiceLocation : BroadcastReceiver() {

    companion object {
        private const val TAG = "StopReceiverR"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "Click on Stoped button !!")

        // stoped service
        context?.stopService(Intent(context, LocationForegroundService::class.java))
    }

}