package com.mkarshnas6.karenstudio.worldskill.ui.screen.telephony

import android.content.Context
import android.telephony.TelephonyManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
fun TelephonyScreen(
    navController: NavController,
    context: Context
) {
    var phoneInfo by remember { mutableStateOf("برای دریافت اطلاعات کلیک کنید") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            // مستقیم اطلاعات رو بگیر - بدون Permission!
            phoneInfo = getTelephonyInfo(context)
        }) {
            Text("دریافت اطلاعات گوشی")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = phoneInfo)
    }
}

fun getTelephonyInfo(context: Context): String {
    val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

    // امن‌ترین روش: try-catch
    val operatorName = try {
        tm.networkOperatorName ?: "نامشخص"
    } catch (e: SecurityException) {
        "نیاز به اجازه"
    }

    val country = try {
        tm.networkCountryIso?.uppercase() ?: "نامشخص"
    } catch (e: SecurityException) {
        "نامشخص"
    }

    val networkType = try {
        getNetworkType(tm.networkType)
    } catch (e: SecurityException) {
        "نامشخص"
    }

    val roaming = try {
        if (tm.isNetworkRoaming) "بله" else "خیر"
    } catch (e: SecurityException) {
        "نامشخص"
    }

    val simCountry = try {
        tm.simCountryIso?.uppercase() ?: "نامشخص"
    } catch (e: SecurityException) {
        "نامشخص"
    }

    val simOperator = try {
        tm.simOperatorName ?: "نامشخص"
    } catch (e: SecurityException) {
        "نامشخص"
    }

    return """
        📱 اطلاعات گوشی:
        
        اپراتور: $operatorName
        کشور: $country
        نوع شبکه: $networkType
        در رومینگ: $roaming
        کد کشور سیم: $simCountry
        اپراتور سیم: $simOperator
    """.trimIndent()
}

fun getNetworkType(type: Int): String = when (type) {
    TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
    TelephonyManager.NETWORK_TYPE_NR -> "5G"
    TelephonyManager.NETWORK_TYPE_UMTS -> "3G"
    TelephonyManager.NETWORK_TYPE_GSM -> "2G"
    else -> "نامشخص ($type)"
}