package com.mkarshnas6.karenstudio.worldskill

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.mkarshnas6.karenstudio.worldskill.data.remote.RetrofitClient
import com.mkarshnas6.karenstudio.worldskill.navigation.AppNavGraph
import com.mkarshnas6.karenstudio.worldskill.ui.theme.WorldSkillTheme
import com.mkarshnas6.karenstudio.worldskill.utils.SharedPrefsManager

class MainActivity : FragmentActivity() {
    private lateinit var prefsManager: SharedPrefsManager

    companion object {
        private const val TAG = "MainActivity"
    }

//    // 👈 Launcher برای درخواست مجوزها
//    private val permissionLauncher = registerForActivityResult(
//        ActivityResultContracts.RequestMultiplePermissions()
//    ) { permissions ->
//        permissions.forEach { (permission, granted) ->
//            Log.d(TAG, "$permission: ${if (granted) "✅ OK" else "❌ NO"}")
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel()
        requestAllPermissions()

        enableEdgeToEdge()

        RetrofitClient.init(this)

        prefsManager = SharedPrefsManager(this)
        setContent {
            WorldSkillTheme {
                AppNavGraph(
                    prefsManager = prefsManager
                )
            }
        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "foreground_service_manager",
            "foregrounded service",
            NotificationManager.IMPORTANCE_LOW
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun requestAllPermissions() {
        val permissions = mutableListOf<String>()

        // 👈 این مجوزها رو همیشه می‌خوایم
        permissions.addAll(listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        // 👈 اینا فقط برای نسخه‌های خاص
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        // 👈 فقط مجوزهایی که گرفته نشدن رو درخواست کن
        val notGranted = permissions.filter { permission ->
            androidx.core.content.ContextCompat.checkSelfPermission(
                this, permission
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            Log.d(TAG, "درخواست مجوزها: $notGranted")
            ActivityCompat.requestPermissions(
                this,
                notGranted.toTypedArray(),
                1
            )
        } else {
            Log.d(TAG, "✅ همه مجوزها قبلاً گرفته شدن")
        }
    }

}
