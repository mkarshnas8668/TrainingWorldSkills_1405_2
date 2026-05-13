package com.mkarshnas6.karenstudio.worldskill.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class NotificationActionReceiver : BroadcastReceiver() {
    companion object {
        private const val TAG = "NotificationActionReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.getStringExtra("action")
        when (action) {
            "stop" -> Log.d(TAG, "action stop is running")
            "open" -> Log.d(TAG, "action open app running")
        }
    }
}