package com.mkarshnas6.karenstudio.worldskill.ui.screen.foregroundService

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.service.LocationForegroundService

@Composable
fun ForegroundServiceScreen(
    navController: NavController,
    context: Context
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // شروع سرویس
            val intent = Intent(context, LocationForegroundService::class.java)
            ContextCompat.startForegroundService(context, intent)
        }) {
            Text("شروع سرویس")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            // توقف سرویس
            val intent = Intent(context, LocationForegroundService::class.java)
            context.stopService(intent)
        }) {
            Text("توقف سرویس")
        }
    }
}
