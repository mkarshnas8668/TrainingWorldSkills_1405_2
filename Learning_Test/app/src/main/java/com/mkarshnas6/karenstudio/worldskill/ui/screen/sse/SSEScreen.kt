package com.mkarshnas6.karenstudio.worldskill.ui.screen.sse

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.gson.Gson
import com.mkarshnas6.karenstudio.worldskill.data.remote.SSE.SSEManager
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.SSEMessage

@Composable
fun SSEScreen() {

    var messages by remember { mutableStateOf<List<String>>(emptyList()) }

    var isConnected by remember { mutableStateOf(false) }

    // create SSE manager
    val sseManager = remember { SSEManager() }

    LaunchedEffect(Unit) {
        sseManager.setListener(object : SSEManager.SEEListener {
            override fun onConnected() {
                isConnected = true
                messages = messages + "connected ✔✔"
            }

            override fun onMessage(message: String) {
                messages = messages + "👇 ${Gson().fromJson(message, SSEMessage::class.java).message}"
            }

            override fun onError(error: String) {
                messages = messages + "error : $error"
            }

        })
        sseManager.connect("user_7")
    }

    // when close == disconnect
    DisposableEffect(Unit) {
        onDispose {
            sseManager.disconnect()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // === هدر: وضعیت اتصال + دکمه‌ها ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isConnected)
                    Color(0xFFE8F5E9)  // سبز
                else
                    Color(0xFFFFEBEE)  // قرمز
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = if (isConnected) "🟢 آنلاین - منتظر پیام..."
                    else "🔴 آفلاین",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "شناسه: user_7",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // === دکمه‌های اتصال/قطع ===
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { sseManager.connect("user_7") },
                modifier = Modifier.weight(1f)
            ) {
                Text("اتصال مجدد")
            }

            Button(
                onClick = { sseManager.disconnect() },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("قطع")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // === عنوان لیست ===
        Text(
            text = "📬 پیام‌های دریافتی:",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // === لیست پیام‌ها ===
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Text(
                        text = "هنوز پیامی دریافت نشده...",
                        color = Color.Gray,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            items(messages.size) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    )
                ) {
                    Text(
                        text = messages[index],
                        modifier = Modifier.padding(12.dp),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

}
