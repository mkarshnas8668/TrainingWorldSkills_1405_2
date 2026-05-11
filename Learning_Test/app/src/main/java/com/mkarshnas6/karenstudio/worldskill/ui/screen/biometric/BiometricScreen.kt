package com.mkarshnas6.karenstudio.worldskill.ui.screen.biometric

import android.content.Context
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R

@Composable
fun BiometricScreen(
    navController: NavController,
    context: Context
) {

    // variable status
    var isAuthenticated by remember { mutableStateOf(false) }
    var statusText by remember { mutableStateOf("is Lock !!") }
    var statusColor by remember { mutableStateOf(Color.Red) }

    // check mobile have biometric
    val biometricManager = remember { BiometricManager.from(context) }
    val canAuthenticate = remember {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> false
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> false
            else -> false
        }
    }

    // ...... create executor & promptInfo..............
    val executor = remember { ContextCompat.getMainExecutor(context) }

    val biometricPrompt: BiometricPrompt = remember {
        BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                    statusText = "احراز هویت با موفقیت انجام شد ✔✔"
                    statusColor = Color.Green
                    Toast.makeText(context, "Welcome !!", Toast.LENGTH_SHORT).show()
                }

                //              wrong
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    isAuthenticated = false
                    statusText = "اشتباه هست . دوباره تلاش کن ❌❌"
                    statusColor = Color.Red
                    Toast.makeText(context, "اشتباه هست . دوباره تلاش کن !!", Toast.LENGTH_SHORT)
                        .show()
                }

                //              error : bad hardware . user canceled
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    isAuthenticated = false
                    statusText = "error : $errString"
                    statusColor = Color.Yellow
                    Toast.makeText(context, "error : $errString", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    // prompt info
    var promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("احراز هویت")
            .setSubtitle("لطفا انگشت کنید .")
            .setDescription("برای باز شدن قفل نیاز به احراز هویت داره . یا پوزت رو بزار یا انگشت کن")
            .setNegativeButtonText("نمی خوام")
            .build()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A2E)),  // پس‌زمینه تیره شیک
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ---------- 6.1 آیکون قفل ----------
            Icon(
                painter = painterResource(if (isAuthenticated) R.drawable.ic_lock_open else R.drawable.ic_lock),
                contentDescription = "قفل",
                modifier = Modifier.size(120.dp),
                tint = if (isAuthenticated) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(30.dp))

            // ---------- 6.2 متن وضعیت ----------
            Text(
                text = statusText,
                color = statusColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ---------- 6.3 متن راهنما (اگه پشتیبانی نمیشه) ----------
            if (!canAuthenticate) {
                Text(
                    text = "⛔ دستگاه شما اثر انگشت یا چهره ندارد!",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "ابتدا در تنظیمات گوشی ثبت کنید",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ---------- 6.4 دکمه احراز هویت ----------
            Button(
                onClick = {
                    biometricPrompt.authenticate(promptInfo)
                },
                modifier = Modifier
                    .size(200.dp, 60.dp),
                enabled = canAuthenticate && !isAuthenticated,  // فقط اگه پشتیبانی بشه و قفل نشده باشه
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F3460),
                    disabledContainerColor = Color.Gray
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(
                    text = "اثر انگشت بزنید ✋",
                    fontSize = 18.sp,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ---------- 6.5 دکمه ریست ----------
            TextButton(
                onClick = {
                    isAuthenticated = false
                    statusText = "قفل بسته است 🔒"
                    statusColor = Color.Red
                }
            ) {
                Text(
                    text = "🔄 ریست کردن / قفل مجدد",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }

}