package com.mkarshnas6.karenstudio.worldskill.ui.encryption

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.utils.encrypte.CryptoManager
import com.mkarshnas6.karenstudio.worldskill.utils.encrypte.KeyManager
import java.io.File

@Composable
fun EncryptionScreen(
    navController: NavController,
    context: Context
) {

    val keyManager = remember { KeyManager() }
    val cryptoManager = remember { CryptoManager(keyManager) }

    var inputText by remember { mutableStateOf("") }

    // state result
    var encryptedHex by remember { mutableStateOf("") } // encrypt HEX
    var decryptedText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf("ready") }
    var encryptedFileName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "encrypt file & text",
            fontSize = 17.sp,
            color = Color.Black
        )
        // status card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                contentColor = Color.Black,
                containerColor = when {
                    statusMessage.contains("✅") -> MaterialTheme.colorScheme.primaryContainer
                    statusMessage.contains("❌") -> MaterialTheme.colorScheme.errorContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            )
        ) {
            Text(
                text = statusMessage,
                modifier = Modifier.padding(12.dp)
            )
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("متن خود را وارد کنید") },
            placeholder = { Text("مثلاً: سلام دنیا!") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Button(
            onClick = {
                try {
                    if (inputText.isBlank()) {
                        statusMessage = "❌ لطفاً متنی وارد کنید"
                        return@Button
                    }

                    val encryptedBytes = cryptoManager.encryptText(inputText)

                    encryptedHex = encryptedBytes.joinToString("") {
                        "%02x".format(it)
                    }

                    decryptedText = ""
                    statusMessage = "✅ متن با موفقیت رمزنگاری شد"

                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("🔒 رمزنگاری متن")
        }

        Button(
            onClick = {
                try {
                    if (encryptedHex.isBlank()) {
                        statusMessage = "❌ ابتدا متن را رمزنگاری کنید"
                        return@Button
                    }

                    val encryptedBytes = encryptedHex.chunked(2)
                        .map { it.toInt(16).toByte() }
                        .toByteArray()

                    decryptedText = cryptoManager.decryptText(encryptedBytes)
                    statusMessage = "✅ متن با موفقیت رمزگشایی شد"

                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                    decryptedText = ""
                }
            },
            modifier = Modifier.weight(1f),
            enabled = encryptedHex.isNotEmpty()
        ) {
            Text("🔓 رمزگشایی")
        }
    }

    if (encryptedHex.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🔒 متن رمز شده (Hex):",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = encryptedHex,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    if (decryptedText.isNotEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "🔓 متن رمزگشایی شده:",
                    style = MaterialTheme.typography.titleSmall
                )
                Text(
                    text = decryptedText,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

    Text(
        text = "📁 رمزنگاری فایل",
        style = MaterialTheme.typography.titleMedium
    )

    // ============ دکمه‌های فایل ============
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // دکمه ۳: رمزنگاری و ذخیره فایل
        Button(
            onClick = {
                try {
                    if (inputText.isBlank()) {
                        statusMessage = "❌ لطفاً متنی وارد کنید"
                        return@Button
                    }

                    val originalFile = File(context.filesDir, "my_note.txt")
                    originalFile.bufferedWriter().use { writer ->
                        writer.write(inputText)
                    }
                    val encryptedFile = File(context.filesDir, "my_note_encrypted.bin")
                    cryptoManager.encryptFile(originalFile, encryptedFile)

                    encryptedFileName = encryptedFile.name
                    statusMessage = "✅ فایل رمزنگاری شد: ${encryptedFile.name}"

                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.weight(1f)
        ) {
            Text("💾 ذخیره رمز شده")
        }

        OutlinedButton(
            onClick = {
                try {
                    val encryptedFile = File(context.filesDir, "my_note_encrypted.bin")

                    if (!encryptedFile.exists()) {
                        statusMessage = "❌ فایل رمز شده وجود ندارد"
                        return@OutlinedButton
                    }

                    // ۱. رمزگشایی
                    val decryptedFile = File(context.filesDir, "my_note_decrypted.txt")
                    cryptoManager.decryptFile(encryptedFile, decryptedFile)

                    // ۲. خوندن نتیجه و نمایش
                    decryptedText = decryptedFile.readText()
                    statusMessage = "✅ فایل رمزگشایی شد"

                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.weight(1f),
            enabled = encryptedFileName.isNotEmpty()
        ) {
            Text("📖 خواندن فایل")
        }
    }

    if (encryptedFileName.isNotEmpty()) {
        Button(
            onClick = {
                try {
                    val encryptedFile = File(context.filesDir, encryptedFileName)

                    if (!encryptedFile.exists()) {
                        statusMessage = "❌ فایل وجود ندارد"
                        return@Button
                    }

                    // ساختن Uri با FileProvider
                    val uri: Uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        encryptedFile
                    )

                    // ارسال
                    val shareIntent = android.content.Intent().apply {
                        action = android.content.Intent.ACTION_SEND
                        type = "application/octet-stream"  // فایل باینری
                        putExtra(android.content.Intent.EXTRA_STREAM, uri)
                        addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }

                    context.startActivity(
                        Intent.createChooser(shareIntent, "ارسال فایل رمز شده")
                    )

                    statusMessage = "📤 در حال ارسال فایل رمز شده"

                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("📤 ارسال فایل رمز شده با FileProvider")
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = "📂 فایل‌های موجود:",
        style = MaterialTheme.typography.titleSmall
    )

    // لیست فایل‌های توی filesDir
    val files = remember { context.filesDir?.listFiles()?.toList() ?: emptyList() }

    if (files.isEmpty()) {
        Text(
            text = "هیچ فایلی وجود ندارد",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    } else {
        files.forEach { file ->
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (file.extension == "bin") "🔒 ${file.name}"
                            else "📄 ${file.name}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "حجم: ${file.length()} بایت",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // دکمه حذف
                    TextButton(
                        onClick = {
                            file.delete()
                            statusMessage = "🗑️ ${file.name} حذف شد"
                        }
                    ) {
                        Text("🗑️")
                    }
                }
            }
        }
    }

}
