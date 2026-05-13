package com.mkarshnas6.karenstudio.worldskill

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

    // 👈 متغیر برای نگهداری وضعیت فعلی (برای نمایش توی UI)
    var batteryStatus by mutableStateOf("---")
    var networkStatus by mutableStateOf("---")

    // 👈 Receiver با رویدادهای قابل تست
    private val systemReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {

                // 🔋 باتری کم (فقط گوشی واقعی)
                Intent.ACTION_BATTERY_LOW -> {
                    Log.d(TAG, "🔋 باتری کم شد!")
                    batteryStatus = "🔋 باتری کم شد!"
                }

                // 🔌 شارژر وصل (فقط گوشی واقعی)
                Intent.ACTION_POWER_CONNECTED -> {
                    Log.d(TAG, "🔌 شارژر وصل شد!")
                    batteryStatus = "🔌 شارژر وصل شد!"
                }

                // 🔌 شارژر قطع (فقط گوشی واقعی)
                Intent.ACTION_POWER_DISCONNECTED -> {
                    Log.d(TAG, "🔌 شارژر قطع شد!")
                    batteryStatus = "🔌 شارژر قطع شد!"
                }

                // ✈️ حالت هواپیما (قابل تست با Emulator!)
                Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                    val isAirplaneMode = intent.getBooleanExtra("state", false)
                    Log.d(TAG, "✈️ حالت هواپیما: $isAirplaneMode")
                    networkStatus = if (isAirplaneMode) "✈️ حالت هواپیما روشن"
                    else "✈️ حالت هواپیما خاموش"
                }

                // 📱 خاموش/روشن شدن صفحه (با Emulator قابل تست!)
                Intent.ACTION_SCREEN_OFF -> {
                    Log.d(TAG, "📱 صفحه خاموش شد")
                    batteryStatus = "📱 صفحه خاموش شد"
                }

                Intent.ACTION_SCREEN_ON -> {
                    Log.d(TAG, "📱 صفحه روشن شد")
                    batteryStatus = "📱 صفحه روشن شد"
                }
            }
        }
    }

    // 👈 ثبت Receiver
    private fun registerSystemReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_LOW)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_SCREEN_OFF)
            addAction(Intent.ACTION_SCREEN_ON)
        }
        registerReceiver(systemReceiver, filter)
        Log.d(TAG, "✅ Receiver ثبت شد")
    }

    // 👈 لغو ثبت
    private fun unregisterSystemReceiver() {
        try {
            unregisterReceiver(systemReceiver)
            Log.d(TAG, "❌ Receiver لغو شد")
        } catch (e: Exception) {
            Log.e(TAG, "Receiver قبلاً لغو شده بود")
        }
    }

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
                    prefsManager = prefsManager,
                    onRegister = { registerSystemReceiver() },
                    onUnregister = { unregisterSystemReceiver() },
                    batteryStatus = batteryStatus,
                    networkStatus = networkStatus
                )
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "foreground_service_manager",
                "foregrounded service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "کانال پیش‌فرض"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun requestAllPermissions() {
        val permissions = mutableListOf<String>()
        permissions.addAll(
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissions.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            permissions.add(Manifest.permission.FOREGROUND_SERVICE_LOCATION)
        }

        val notGranted = permissions.filter { permission ->
            ContextCompat.checkSelfPermission(this, permission) !=
                    android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                notGranted.toTypedArray(),
                200
            )
        } else {
            Log.d(TAG, "✅ همه مجوزها گرفته شدن")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterSystemReceiver()
    }
}