package com.mkarshnas6.karenstudio.worldskill.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class StaticBootReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "StaticBootReceiver"
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"mobile is on || now do every thing you want 👌👌")
    }

}