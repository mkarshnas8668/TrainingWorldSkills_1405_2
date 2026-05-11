package com.mkarshnas6.karenstudio.worldskill.ui.screen.clipboard

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ClipboardScreen(
    navController: NavController,
    context: Context
) {
    val clipboardManager =
        remember { context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager }

    var inputText by remember { mutableStateOf("") }
    var pastedText by remember { mutableStateOf("متن past شده اینجا نمایش داده میشود") }

    val darkBackground = Color(0xFF1A1A2E)
    val cardBackground = Color(0xFF16213E)
    val accentColor = Color(0xFF0F3460)
    val greenColor = Color(0xFF4CAF50)
    val orangeColor = Color(0xFFFF9800)

    // ================== UI ==================
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(darkBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // ===== عنوان صفحه =====
            Text(
                text = "📋 Clipboard",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ===== کارت ورودی متن =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "✏️ متن خود را وارد کنید:",
                        color = Color.White,
                        fontSize = 16.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // فیلد متنی
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("متن را اینجا بنویسید...", color = Color.Gray) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = accentColor,
                            unfocusedBorderColor = Color.Gray
                        ),
                        maxLines = 3
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ===== دکمه‌های عملیات =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // دکمه کپی
                Button(
                    onClick = {
                        copyToClipboard(context, clipboardManager, inputText)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = greenColor),
                    shape = RoundedCornerShape(12.dp),
                    enabled = inputText.isNotEmpty()
                ) {
                    Text("📝 کپی", color = Color.White, fontSize = 16.sp)
                }

                // دکمه Paste
                Button(
                    onClick = {
                        pastedText = pasteFromClipboard(clipboardManager)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .padding(start = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = orangeColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("📌 Paste", color = Color.White, fontSize = 16.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ===== کارت متن Paste شده =====
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardBackground)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "📄 متن Paste شده:",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // نمایش متن Paste شده
                    Text(
                        text = pastedText,
                        color = if (pastedText == "متن Paste شده اینجا نمایش داده میشه")
                            Color.Gray
                        else
                            Color.White,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ===== دکمه پاک کردن =====
            OutlinedButton(
                onClick = {
                    clearClipboard(clipboardManager)
                    pastedText = "متن Paste شده اینجا نمایش داده میشه"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
            ) {
                Text("🗑️ پاک کردن کلیپ‌بورد", fontSize = 16.sp)
            }
        }
    }

}

private fun copyToClipboard(
    context: Context,
    clipboardManager: ClipboardManager,
    text: String
) {
    clipboardManager.setPrimaryClip(ClipData.newPlainText("", text))
    Toast.makeText(context, "✅ متن کپی شد!", Toast.LENGTH_SHORT).show()
}

private fun pasteFromClipboard(clipboardManager: ClipboardManager): String {
    if (!clipboardManager.hasPrimaryClip()) {
        return "❌ کلیپ‌بورد خالی است!"
    }
    val clipData = clipboardManager.primaryClip ?: return "❌ خطا در خواندن کلیپ‌بورد!"
    val item = clipData.getItemAt(0)
    return item.text?.toString() ?: "❌ متن قابل خواندن نیست!"
}

private fun clearClipboard(clipboardManager: ClipboardManager) {
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
        clipboardManager.clearPrimaryClip()
    } else {
        val emptyClip = ClipData.newPlainText("", "")
        clipboardManager.setPrimaryClip(emptyClip)
    }
}