package com.mkarshnas6.karenstudio.worldskill.ui.screen.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R
import com.mkarshnas6.karenstudio.worldskill.notification.NotificationHelper

@Composable
fun NotificationScreen(
    navController: NavController,
    context: Context
) {
    val helper = remember { NotificationHelper(context) }

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(
            context as FragmentActivity,
            listOf(Manifest.permission.POST_NOTIFICATIONS).toTypedArray(),
            19
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("🔔 Notification Test", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        // دکمه ۱: ساخت کانال
        Button(onClick = { helper.createChannel() }) {
            Text("📡 ساخت کانال")
        }

        // دکمه ۲: نوتیفیکیشن ساده
        Button(onClick = {
            helper.showSimpleNotification(
                "سلام!",
                "این یه نوتیفیکیشن ساده‌ست",
                R.mipmap.ic_launcher_round
            )
        }) {
            Text("🔔 نوتیفیکیشن ساده")
        }

        // دکمه ۳: نوتیفیکیشن با کلیک
        Button(onClick = {
            helper.showNotificationWithClick(
                "کلیک کن",
                "با کلیک اپ باز میشه",
                R.mipmap.ic_launcher_round
            )
        }) {
            Text("👆 نوتیفیکیشن با کلیک")
        }

        // دکمه ۴: نوتیفیکیشن با دکمه
        Button(onClick = {
            helper.showNotificationWithAction(
                "دکمه دار",
                "این نوتیفیکیشن دکمه داره",
                R.mipmap.ic_launcher_round
            )
        }) {
            Text("🎮 نوتیفیکیشن با دکمه")
        }

        // دکمه ۵: گروهی
        Button(onClick = { helper.showGroupNotifications() }) {
            Text("📦 نوتیفیکیشن گروهی")
        }
    }
}