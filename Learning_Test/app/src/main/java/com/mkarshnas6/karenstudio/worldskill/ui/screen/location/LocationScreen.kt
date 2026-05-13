package com.mkarshnas6.karenstudio.worldskill.ui.screen.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import java.util.Locale

@Composable
fun LocationScreen(
    navController: NavController,
    context: Context
) {

    var latitude by remember { mutableStateOf("---") }
    var longitude by remember { mutableStateOf("---") }
    var providerName by remember { mutableStateOf("---") }
    var accuracy by remember { mutableStateOf("---") }
    var speed by remember { mutableStateOf("---") }
    var altitude by remember { mutableStateOf("---") }
    var isTracking by remember { mutableStateOf(false) }

    // this variable select we use which provider
    var selectedProvider by remember { mutableStateOf("auto") }

    // 4️⃣ LocationManager و Listener رو اینجا نگه می‌داریم
    val locationManager = remember {
        context.getSystemService(LocationManager::class.java)
    }

    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = String.format(Locale.US, "%.6f", location.latitude)
                longitude = String.format(Locale.US, "%.6f", location.longitude)
                providerName = location.provider ?: "Unknown"
                accuracy = "${location.accuracy} meter"
                speed = if (location.hasSpeed()) "${location.speed}" else "---"
                altitude = if (location.hasAltitude()) "${location.altitude} meter" else "---"
            }

            override fun onProviderDisabled(provider: String) {
                providerName = "$provider is off"
            }

        }
    }

    // 5️⃣ UI ساده
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📍 Location Services",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 👈 انتخاب Provider (GPS یا Network یا Auto)
        Text("انتخاب Provider:", style = MaterialTheme.typography.titleMedium)

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // دکمه GPS
            FilterChip(
                onClick = { selectedProvider = "gps" },
                label = { Text("GPS") },
                selected = selectedProvider == "gps"
            )
            // دکمه Network
            FilterChip(
                onClick = { selectedProvider = "network" },
                label = { Text("Network") },
                selected = selectedProvider == "network"
            )
            // دکمه Auto (Criteria)
            FilterChip(
                onClick = { selectedProvider = "auto" },
                label = { Text("Auto") },
                selected = selectedProvider == "auto"
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 👈 نمایش اطلاعات موقعیت
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                InfoRow("Latitude", latitude)
                InfoRow("Longitude", longitude)
                InfoRow("Provider", providerName)
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                InfoRow("Accuracy", accuracy)
                InfoRow("Speed", speed)
                InfoRow("Altitude", altitude)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 👈 دکمه شروع ردیابی
        Button(
            onClick = {
                startTracking(
                    context = context,
                    locationManager = locationManager,
                    listener = locationListener,
                    providerType = selectedProvider
                )
                isTracking = true
            },
            enabled = !isTracking
        ) {
            Text("▶ شروع ردیابی")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 👈 دکمه توقف
        Button(
            onClick = {
                locationManager.removeUpdates(locationListener)
                isTracking = false
            },
            enabled = isTracking
        ) {
            Text("⏹ توقف ردیابی")
        }
    }

}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
    }
}

// ─── تابع شروع ردیابی (مهم‌ترین بخش!) ────────────────────
fun startTracking(
    context: Context,
    locationManager: LocationManager,
    listener: LocationListener,
    providerType: String  // "gps" یا "network" یا "auto"
) {
    val TAG = "LocationService"
    // 1️⃣ اول چک کن مجوز داری یا نه
    if (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        Log.e(TAG, "❌ مجوز موقعیت ندارید!")
        return  // بیا بیرون، ادامه نده
    }

    // 2️⃣ انتخاب Provider بر اساس انتخاب کاربر
    val provider = when (providerType) {
        "gps" -> {
            Log.d(TAG, "📍 استفاده از GPS")
            LocationManager.GPS_PROVIDER
        }

        "network" -> {
            Log.d(TAG, "📍 استفاده از Network")
            LocationManager.NETWORK_PROVIDER
        }

        else -> {
            // 👈 اینجا Criteria رو می‌سازیم (انتخاب خودکار)
            Log.d(TAG, "📍 استفاده از Criteria (خودکار)")
            val criteria = Criteria().apply {
                // دقت بالا می‌خوایم
                accuracy = Criteria.ACCURACY_FINE
                // مصرف باتری کم باشه
                powerRequirement = Criteria.POWER_LOW
                // ارتفاع هم می‌خوایم
                isAltitudeRequired = true
                // سرعت هم می‌خوایم
                isSpeedRequired = true
            }
            // بهترین Provider رو برامون انتخاب کن
            locationManager.getBestProvider(criteria, true)
                ?: LocationManager.GPS_PROVIDER  // اگه هیچی نبود، GPS
        }
    }

    // 3️⃣ آخرین موقعیت شناخته شده رو بگیر (اختیاری)
    val lastLocation = locationManager.getLastKnownLocation(provider)
    lastLocation?.let {
        Log.d(TAG, "📍 آخرین موقعیت: ${it.latitude}, ${it.longitude}")
    }

    // 4️⃣ شروع ردیابی
    locationManager.requestLocationUpdates(
        provider,     // 👈 کدوم Provider؟
        1000L,        // 👈 هر ۲ ثانیه آپدیت بده
        1f,           // 👈 یا با ۵ متر جابجایی
        listener      // 👈 به این گوش بده
    )

    Log.d(TAG, "✅ ردیابی با $provider شروع شد")
}