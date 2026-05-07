package com.mkarshnas6.karenstudio.worldskill.ui.fileProvider

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
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import java.io.File

@Composable
fun FileProviderSimpleScreen(
    navController: NavController,
    context: Context
) {
    // the first create provider in manifest and xml

    var fileName by remember { mutableStateOf("test_file.txt") }
    var fileContent by remember { mutableStateOf("this is the test file text !!") }
    var statusMessage by remember { mutableStateOf("ready") }

    fun saveFile(): File {
        val file = File(context.filesDir, fileName)
        file.writeText(fileContent)
        return file
    }

    fun shareFile(file:File){
        val authority = "${context.packageName}.fileprovider"
        val uri: Uri = FileProvider.getUriForFile(
            context,
            authority,
            file
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_STREAM,uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(
            shareIntent,
            "... ارسال فایل با"
        )
        context.startActivity(chooser)
    }

    // ============ ۵. UI ============
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // عنوان
        Text(
            text = "📤 FileProvider ساده",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // وضعیت
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = "وضعیت: $statusMessage",
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // فیلد نام فایل
        OutlinedTextField(
            value = fileName,
            onValueChange = { fileName = it },
            label = { Text("نام فایل") },
            placeholder = { Text("مثلاً: note.txt") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // فیلد محتوا
        OutlinedTextField(
            value = fileContent,
            onValueChange = { fileContent = it },
            label = { Text("متن فایل") },
            placeholder = { Text("هر چی دوست داری بنویس...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            maxLines = 10
        )

        Spacer(modifier = Modifier.height(24.dp))

        // دکمه ۱: ذخیره فایل
        Button(
            onClick = {
                try {
                    val savedFile = saveFile()
                    statusMessage = "✅ فایل ذخیره شد: ${savedFile.name}"
                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("💾 ذخیره فایل در حافظه داخلی")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // دکمه ۲: ارسال فایل
        Button(
            onClick = {
                try {
                    // اول فایل رو ذخیره کن
                    val savedFile = saveFile()
                    statusMessage = "✅ فایل آماده شد! در حال باز کردن منوی اشتراک..."

                    // بعد بفرست
                    shareFile(savedFile)

                    // statusMessage رو بعد از بسته شدن اشتراک آپدیت کن
                    // (این قسمت دقیق نیست چون نمی‌دونیم کی کاربر برمی‌گرده)

                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Text("📤 ارسال فایل با FileProvider")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // دکمه ۳: فقط ارسال (بدون ذخیره مجدد)
        OutlinedButton(
            onClick = {
                try {
                    // فایل رو بدون ذخیره مجدد پیدا کن
                    val existingFile = File(context.filesDir, fileName)

                    // چک کن فایل وجود داره؟
                    if (existingFile.exists()) {
                        shareFile(existingFile)
                        statusMessage = "📤 در حال ارسال ${existingFile.name}"
                    } else {
                        statusMessage = "❌ فایل وجود ندارد! اول ذخیره کن."
                    }
                } catch (e: Exception) {
                    statusMessage = "❌ خطا: ${e.message}"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📤 ارسال فایل موجود (بدون ذخیره مجدد)")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ============ ۶. نمایش فایل‌های موجود ============
        Text(
            text = "📂 فایل‌های ذخیره شده:",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        // گرفتن لیست فایل‌ها
        val existingFiles = context.filesDir?.listFiles()?.toList() ?: emptyList()

        if (existingFiles.isEmpty()) {
            Text(
                text = "هنوز هیچ فایلی ذخیره نشده",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            // نمایش لیست فایل‌ها
            existingFiles.forEach { file ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "📄 ${file.name}",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "حجم: ${file.length()} بایت",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // دکمه ارسال برای هر فایل
                        TextButton(
                            onClick = {
                                shareFile(file)
                                statusMessage = "📤 در حال ارسال ${file.name}"
                            }
                        ) {
                            Text("📤 ارسال")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ============ ۷. توضیح FileProvider ============
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🔍 FileProvider چطور کار می‌کنه؟",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = """
                        ۱. فایل رو توی filesDir ذخیره می‌کنیم
                        ۲. FileProvider یه URI امن میسازه:
                           file:// ❌ (قدیمی - ناامن)
                           content:// ✅ (جدید - امن)
                        ۳. به اپ مقصد اجازه موقت میدیم فایل رو بخونه
                        ۴. کاربر انتخاب می‌کنه با کدوم اپ باز کنه
                    """.trimIndent(),
                )
            }
        }
    }
}
