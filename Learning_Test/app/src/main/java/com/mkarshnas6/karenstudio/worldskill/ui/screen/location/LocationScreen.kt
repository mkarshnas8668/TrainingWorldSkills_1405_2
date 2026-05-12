package com.mkarshnas6.karenstudio.worldskill.ui.screen.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
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

@Composable
fun LocationScreen(
    navController: NavController,
    context: Context
) {

    var latitude by remember { mutableStateOf("---") }
    var longitude by remember { mutableStateOf("---") }
    var provider by remember { mutableStateOf("---") }
    var isTracking by remember { mutableStateOf(false) }

    // 4️⃣ LocationManager و Listener رو اینجا نگه می‌داریم
    val locationManager = remember {
        context.getSystemService(LocationManager::class.java)
    }

    val locationListener = remember {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                // هر بار موقعیت جدید میاد، این اجرا میشه
                latitude = location.latitude.toString()
                longitude = location.longitude.toString()
                provider = location.provider ?: "unknown"
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

        Spacer(modifier = Modifier.height(30.dp))

        // نمایش مختصات
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Latitude: $latitude")
                Text("Longitude: $longitude")
                Text("Provider: $provider")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // دکمه شروع ردیابی
        Button(
            onClick = {
                // 6️⃣ چک کردن مجوز
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    // 7️⃣ شروع ردیابی
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,  // 👈 از GPS استفاده کن
                        1000L,                          // 👈 هر ۱ ثانیه آپدیت بده
                        1f,                             // 👈 یا با ۱ متر جابجایی
                        locationListener                // 👈 به این شنونده خبر بده
                    )
                    isTracking = true
                }
            },
            enabled = !isTracking
        ) {
            Text("▶ شروع ردیابی")
        }

        // دکمه توقف
        Button(
            onClick = {
                // 8️⃣ توقف ردیابی
                locationManager.removeUpdates(locationListener)
                isTracking = false
            },
            enabled = isTracking
        ) {
            Text("⏹ توقف ردیابی")
        }
    }

}