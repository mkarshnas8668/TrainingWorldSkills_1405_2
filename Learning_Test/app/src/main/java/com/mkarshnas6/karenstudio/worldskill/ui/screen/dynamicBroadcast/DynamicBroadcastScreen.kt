package com.mkarshnas6.karenstudio.worldskill.ui.screen.dynamicBroadcast

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.navigation.NavController

@Composable
fun DynamicBroadcastScreen(
    onRegister: () -> Unit,
    onUnregister: () -> Unit,
    navController: NavController,
    context: Context,
    batteryStatus: String = "---",
    networkStatus: String = "---"
) {
    var isRegistered by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "📡 Broadcast Receiver Test",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        // 👈 نمایش وضعیت‌ها
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("🔋 باتری: $batteryStatus")
                Text("📶 شبکه: $networkStatus")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isRegistered) "🟢 در حال گوش دادن..."
            else "🔴 گوش دادن متوقف شد",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                onRegister()
                isRegistered = true
            },
            enabled = !isRegistered
        ) {
            Text("👂 شروع گوش دادن")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                onUnregister()
                isRegistered = false
            },
            enabled = isRegistered
        ) {
            Text("🔇 توقف گوش دادن")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("💡 راهنمای تست:", style = MaterialTheme.typography.titleSmall)
                Text("• دکمه پاور گوشی رو بزن (صفحه روشن/خاموش)")
                Text("• حالت هواپیما رو روشن/خاموش کن")
                Text("• رویداد باتری فقط روی گوشی واقعی کار میکنه")
            }
        }
    }
}