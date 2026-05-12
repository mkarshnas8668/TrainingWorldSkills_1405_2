package com.mkarshnas6.karenstudio.worldskill.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log


// ❌❌❌❌ Attention : services must write in manifest  ❌❌❌❌
class BaseAllService : Service() {
    companion object {
        private const val TAG = "MyFirstService"
    }

    override fun onCreate() {
        // create service : call when create service
        super.onCreate()
        Log.d(TAG, "on create : Created Service")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // start command : call when start service to work || do some thing
        Log.d(TAG, "on Start Command : Start Service to work")
//        startForeground() // call this for start work in foreground
        return START_STICKY // start sticky mean : after kill service restart again
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "on Destroy : Service destroy")
    }

}