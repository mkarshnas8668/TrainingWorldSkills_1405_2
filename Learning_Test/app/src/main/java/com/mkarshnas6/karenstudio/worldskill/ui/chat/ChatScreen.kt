package com.mkarshnas6.karenstudio.worldskill.ui.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkarshnas6.karenstudio.worldskill.data.remote.WS.WebSocketManager

@Composable
fun ChatScreen(userId: String = "user_5") {
    // ۱. متغیرها برای UI
    var messages by remember { mutableStateOf<List<String>>(emptyList()) }
    var inputText by remember { mutableStateOf("") }
    var isConnected by remember { mutableStateOf(false) }

    // ۲. WebSocketManager رو بساز
    val wsManager = remember { WebSocketManager() }

    // ۳. وقتی Screen باز شد، وصل شو
    LaunchedEffect(Unit) {
        // ۳.۱ Listener رو تنظیم کن
        wsManager.setListener(object : WebSocketManager.WebSocketListener {
            override fun onConnected(clientId: String) {
                isConnected = true
                messages = messages + "✅ متصل شدیم"
            }

            override fun onDisconnected() {
                isConnected = false
                messages = messages + "❌ قطع شد"
            }

            override fun onError(error: String) {
                messages = messages + "💥 خطا: $error"
            }

            override fun onChatMessage(message: WebSocketManager.IncomingChatMessage) {
                // پیام جدید! به لیست اضافه کن
                messages = messages + "${message.sender_id}: ${message.message}"
            }

            override fun onOnlineUsers(users: WebSocketManager.OnlineUsersResponse) {
                messages = messages + "👥 آنلاین: ${users.users.joinToString()}"
            }

            override fun onTypingNotification(typing: WebSocketManager.TypingNotification) {
                if (typing.is_typing) {
                    messages = messages + "✍️ ${typing.user_id} در حال تایپ..."
                }
            }
        })

        // ۳.۲ وصل شو
        wsManager.connect("user", userId.removePrefix("user_"))
    }

    // ۴. وقتی Screen بسته شد، قطع کن
    DisposableEffect(Unit) {
        onDispose {
            wsManager.disconnect()
        }
    }

    // ۵. UI
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        // وضعیت
        Text(if (isConnected) "🟢 آنلاین" else "🔴 آفلاین", fontSize = 18.sp)

        // لیست پیام‌ها
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(messages.size) { index ->
                Text(messages[index], modifier = Modifier.padding(4.dp))
            }
        }

        // ورودی و دکمه ارسال
        Row {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("پیام...") }
            )

            Button(onClick = {
                if (inputText.isNotBlank()) {
                    wsManager.sendingMessage("admin", inputText)  // 👈 ارسال
                    messages = messages + "شما: $inputText"
                    inputText = ""
                }
            }) {
                Text("ارسال")
            }
        }

        // دکمه‌های اضافه
        Row {
            Button(onClick = { wsManager.requestOnlineUsers() }) {
                Text("کاربران آنلاین")
            }
            Button(onClick = { wsManager.sendTypingStatus("admin", true) }) {
                Text("تایپ...")
            }
        }
    }
}